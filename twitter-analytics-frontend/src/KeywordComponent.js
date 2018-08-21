import React, {Component} from 'react';
import {Card, CardBody, CardImg, CardSubtitle, CardTitle, Col} from "reactstrap";
import Chart from 'chart.js';

class KeywordComponent extends Component {

  constructor() {
    super();
    this.state = {
      myState: 0
    }
  }

  componentDidMount() {
    if (this.props.comp.keyword === 'Microsoft')
      this.createTrendsBar();
    if (this.props.comp.keyword === 'Google')
      this.createLineChart();
    if (this.props.comp.keyword === 'Feevale')
      this.createDoughnut();

  }

  createTrendsBar() {
    new Chart(this.node, {
      type: "doughnut",
      data: {
        labels: ["Positive", "Negative", "Neutral"],
        datasets: [
          {
            data: [12, 19, 3],
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

  createDoughnut() {
    new Chart(this.node, {
      type: 'bar',
      data: {
        labels: ['Surface', 'Windows', 'Mobile'],
        datasets: [{
          label: 'Positive',
          borderColor: "rgb(20, 168, 27)",
          backgroundColor: "rgb(20, 168, 27)",
          data: [60, 70, 30]
        }, {
          borderColor: "rgb(229, 107, 107)",
          backgroundColor: "rgb(229, 107, 107)",
          label: 'Negative',
          data: [30, 20, 50]
        }, {
          borderColor: "rgb(232, 229, 67)",
          backgroundColor: "rgb(232, 229, 67)",
          label: 'Neutral',
          data: [10, 20, 15]
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
              max: 100,
              min: 0
            }
          }, {
            id: 'B',
            type: 'linear',
            position: 'right',
            ticks: {
              max: 100,
              min: 0
            },
          }]
        }
      }
    });
  }

  createLineChart() {
    new Chart(this.node, {
      type: 'line',
      data: {
        labels: ['January', 'February', 'March', 'April', 'May'],
        datasets: [{
          borderColor: "rgb(20, 168, 27)",
          backgroundColor: "rgb(20, 168, 27)",
          label: 'Positive',
          yAxisID: 'A',
          fill: false,
          data: [40, 50, 40, 60, 70]
        }, {
          borderColor: "rgb(229, 107, 107)",
          backgroundColor: "rgb(229, 107, 107)",
          label: 'Negative',
          fill: false,
          yAxisID: 'B',
          data: [40, 30, 50, 30, 40]
        }, {
          borderColor: "rgb(232, 229, 67)",
          backgroundColor: "rgb(232, 229, 67)",
          label: 'Neutral',
          fill: false,
          yAxisID: 'B',
          data: [10, 20, 15, 15, 20]
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
              max: 100,
              min: 0
            }
          }, {
            id: 'B',
            type: 'linear',
            position: 'right',
            ticks: {
              max: 100,
              min: 0
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
            <CardTitle>{this.props.comp.keyword}</CardTitle>
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

export default KeywordComponent;
