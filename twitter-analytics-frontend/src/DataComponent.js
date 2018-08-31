import React, {Component} from 'react';
import {Card, CardBody, CardSubtitle, CardTitle, Col, Input, Row} from "reactstrap";
import Chart from 'chart.js';
import Cookies from 'js-cookie';
import QueryString from 'querystring';
import {IconContext} from "react-icons";
import {MdCheck, MdClear, MdDelete, MdEdit} from "react-icons/md";

class DataComponent extends Component {

  constructor(props) {
    super(props);
    this.state = {
      selfUrl: this.props.comp._links.self.href,
      keyword: this.props.comp.keyword,
      from: this.props.comp.from,
      to: this.props.comp.to,
      type: this.props.comp.type,
      editMode: false,
      editKeyword: this.props.comp.keyword,
      editFrom: this.props.comp.from,
      editTo: this.props.comp.to,
      editType: this.props.comp.type,
      enabled: true
    };
    this.chart = null;
  }

  componentDidMount() {
    this.fetchData();
  }

  fetchData() {
    let params = {
      keyword: this.state.keyword,
      from: this.state.from,
      to: this.state.to,
    };
    let path = this.state.type === 'chart' ? 'chart?' : 'trends?';
    fetch('http://localhost:8080/analytics/' + path + QueryString.stringify(params), {
      headers: new Headers({
        'Authorization': 'Bearer ' + Cookies.get('access_token'),
      }),
    })
      .then(resp => resp.json())
      .then(json => {
        let oldChart = this.chart;

        if (this.state.type === 'pie')
          this.buildPieChart(json);
        else if (this.state.type === 'trends')
          this.buildTrendsChart(json);
        else if (this.state.type === 'chart')
          this.buildLineChart(json);

        if (oldChart)
          oldChart.destroy();
      });
  }

  buildPieChart(json) {
    let sentiments = [
      json.sentiments.positive,
      json.sentiments.negative,
      json.sentiments.neutral
    ];
    this.chart = new Chart(this.node, {
      type: "doughnut",
      data: {
        labels: ["Positive", "Negative", "Neutral"],
        datasets: [
          {
            data: sentiments,
            backgroundColor: [
              "rgb(20, 168, 27)",
              "rgb(229, 107, 107)",
              "rgb(232, 229, 67)"
            ]
          }
        ]
      }
    });
  }

  buildTrendsChart(json) {
    let labels = Object.keys(json.trends);
    let positives = [];
    let neutrals = [];
    let negatives = [];

    for (const key of labels) {
      positives.push(json.trends[key].positive);
      negatives.push(json.trends[key].negative);
      neutrals.push(json.trends[key].neutral);
    }

    const max = Math.max(...(negatives.concat(positives).concat(neutrals)));

    this.chart = new Chart(this.node, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Positive',
          borderColor: "rgb(20, 168, 27)",
          backgroundColor: "rgb(20, 168, 27)",
          data: positives
        }, {
          borderColor: "rgb(229, 107, 107)",
          backgroundColor: "rgb(229, 107, 107)",
          label: 'Negative',
          data: negatives
        }, {
          borderColor: "rgb(232, 229, 67)",
          backgroundColor: "rgb(232, 229, 67)",
          label: 'Neutral',
          data: neutrals
        }]
      },
      options: {
        stacked: false,
        scales: {
          yAxes: [{
            id: 'A',
            type: 'linear',
            position: 'left',
            ticks: {
              suggestedMax: max,
              min: 0
            }
          }, {
            id: 'B',
            type: 'linear',
            position: 'right',
            ticks: {
              suggestedMax: max,
              min: 0
            },
          }]
        }
      }
    });
  }

  buildLineChart(json) {
    let labels = Object.keys(json);
    let positives = [];
    let neutrals = [];
    let negatives = [];

    for (const key of labels) {
      positives.push(json[key].positive);
      negatives.push(json[key].negative);
      neutrals.push(json[key].neutral);
    }

    const max = Math.max(...(negatives.concat(positives).concat(neutrals)));
    const min = Math.min(...(negatives.concat(positives).concat(neutrals)));

    this.chart = new Chart(this.node, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          borderColor: "rgb(20, 168, 27)",
          backgroundColor: "rgb(20, 168, 27)",
          label: 'Positive',
          yAxisID: 'A',
          fill: false,
          data: positives
        }, {
          borderColor: "rgb(229, 107, 107)",
          backgroundColor: "rgb(229, 107, 107)",
          label: 'Negative',
          fill: false,
          yAxisID: 'B',
          data: negatives
        }, {
          borderColor: "rgb(232, 229, 67)",
          backgroundColor: "rgb(232, 229, 67)",
          label: 'Neutral',
          fill: false,
          yAxisID: 'B',
          data: neutrals
        }]
      },
      options: {
        stacked: false,
        scales: {
          yAxes: [{
            id: 'A',
            type: 'linear',
            position: 'left',
            ticks: {
              suggestedMax: max,
              suggestedMin: min
            }
          }, {
            id: 'B',
            type: 'linear',
            position: 'right',
            ticks: {
              suggestedMax: max,
              suggestedMin: min
            },
            gridLines: {
              drawOnChartArea: false,
            },
          }]
        }
      }
    });
  }

  static capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  dismissChanges(e) {
    e.preventDefault();
    this.setState({
      editMode: false,
      editKeyword: this.state.keyword,
      editType: this.state.type,
      editFrom: this.state.from,
      editTo: this.state.to
    });
  }

  removeComponent(e) {
    e.preventDefault();
    fetch(this.state.selfUrl, {
      method: 'DELETE',
      headers: new Headers({
        'Authorization': 'Bearer ' + Cookies.get('access_token'),
      })
    })
      .then(() => {
        this.chart.destroy();
        this.setState({enabled: false});
      });
  }

  saveChanges(e) {
    e.preventDefault();
    this.setState({
      editMode: false,
      keyword: this.state.editKeyword,
      type: this.state.editType,
      from: this.state.editFrom,
      to: this.state.editTo
    });
    fetch(this.state.selfUrl, {
      method: 'PATCH',
      headers: new Headers({
        'Authorization': 'Bearer ' + Cookies.get('access_token'),
        'Content-Type': 'application/json'
      }),
      body: JSON.stringify({
        keyword: this.state.editKeyword,
        type: this.state.editType,
        from: this.state.editFrom,
        to: this.state.editTo
      })
    })
      .then(() => this.fetchData());
  }

  render() {
    if (!this.state.enabled) {
      return null;
    }
    let icons;
    let iconConfig = {size: '1.5em'};
    if (!this.state.editMode) {
      icons = (
        <div>
          <IconContext.Provider value={iconConfig}>
            <a href={'# '} onClick={(e) => {
              this.setState({editMode: true});
              e.preventDefault();
            }}><MdEdit/></a>
          </IconContext.Provider>
          <IconContext.Provider value={iconConfig}>
            <a href={'# '} onClick={this.removeComponent.bind(this)}><MdDelete/></a>
          </IconContext.Provider>
        </div>
      )
    } else {
      icons = (
        <div>
          <IconContext.Provider value={iconConfig}>
            <a href={'# '} onClick={this.saveChanges.bind(this)}><MdCheck/></a>
          </IconContext.Provider>
          <IconContext.Provider value={iconConfig}>
            <a href={'# '} onClick={this.dismissChanges.bind(this)}><MdClear/></a>
          </IconContext.Provider>
        </div>
      );
    }

    return (
      <Col lg='6'>
        <Card>
          <CardBody className={this.state.editMode ? 'edit-mode' : ''}>
            <Row>
              <Col xs='10' sm='9'>
                {!this.state.editMode ?
                  (<div>
                      <CardTitle>{this.state.keyword}</CardTitle>
                      <CardSubtitle>From {this.state.from} to {this.state.to}</CardSubtitle>
                    </div>
                  ) :
                  (<div>
                      <Input value={this.state.editKeyword} className='keyword card-input'
                             onChange={(e) => this.setState({editKeyword: e.target.value})}/>
                      <Input type="select" value={DataComponent.capitalizeFirstLetter(this.state.editType)}
                             className='card-input'
                             onChange={(e) => this.setState({editType: e.target.value.toLowerCase()})}>
                        <option>Pie</option>
                        <option>Chart</option>
                        <option>Trends</option>
                      </Input>
                      <CardSubtitle>
                        <span>From </span>
                        <Input value={this.state.editFrom} className='input-date card-input'
                               onChange={e => this.setState({editFrom: e.target.value})}/>
                        <span> to </span>
                        <Input value={this.state.editTo} className='input-date card-input'
                               onChange={e => this.setState({editTo: e.target.value})}/>
                      </CardSubtitle>
                    </div>
                  )
                }
              </Col>
              <Col xs='2' sm='3' className={'text-right'}>
                {icons}
              </Col>
            </Row>
          </CardBody>
          <canvas style={{width: 900, height: 475}} ref={node => (this.node = node)}/>
        </Card>
      </Col>
    );
  }

}

export default DataComponent;
