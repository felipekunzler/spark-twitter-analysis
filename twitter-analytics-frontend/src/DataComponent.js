import React, {Component} from 'react';
import {Button, Card, CardBody, CardSubtitle, CardTitle, Col} from 'reactstrap';
import Chart from 'chart.js';
import Cookies from 'js-cookie';
import QueryString from 'querystring';

class DataComponent extends Component {

  constructor(props) {
    super(props);
    this.state = {
      keyword: this.props.comp.keyword,
      from: this.props.comp.from,
      to: this.props.comp.to,
      type: this.props.comp.type
    }
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
        if (this.state.type === 'pie')
          this.buildPieChart(json);
        else if (this.state.type === 'trends')
          this.buildTrendsChart(json);
        else if (this.state.type === 'chart')
          this.buildLineChart(json);
      });
  }

  buildPieChart(json) {
    let sentiments = [
      json.sentiments.positive,
      json.sentiments.negative,
      json.sentiments.neutral
    ];
    new Chart(this.node, {
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
    let negatives  = [];

    for (const key of labels) {
      positives.push(json.trends[key].positive);
      negatives.push(json.trends[key].negative);
      neutrals.push(json.trends[key].neutral);
    }

    const max = Math.max(...(negatives.concat(positives).concat(neutrals)));

    new Chart(this.node, {
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
    let negatives  = [];

    for (const key of labels) {
      positives.push(json[key].positive);
      negatives.push(json[key].negative);
      neutrals.push(json[key].neutral);
    }

    const max = Math.max(...(negatives.concat(positives).concat(neutrals)));
    const min = Math.min(...(negatives.concat(positives).concat(neutrals)));

    new Chart(this.node, {
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

  render() {
    return (
      <Col lg='6'>
        <Card>
          <CardBody>
            <CardTitle onClick={this.fetchData.bind(this)}>{this.props.comp.keyword}</CardTitle>
            <CardSubtitle>From {this.props.comp.from} to {this.props.comp.to}</CardSubtitle>
          </CardBody>
          <canvas
            style={{width: 900, height: 475}}
            ref={node => (this.node = node)}
          />
        </Card>
      </Col>
    );
  }

}

export default DataComponent;
