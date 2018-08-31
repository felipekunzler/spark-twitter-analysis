import React, {Component} from 'react';
import Cookies from 'js-cookie';
import {Card, CardBody, CardSubtitle, CardTitle, Col, Container, Row} from "reactstrap";
import Chart from "chart.js";
import DataComponent from "./DataComponent";

class Dashboard extends Component {

  constructor() {
    super();
    this.state = {
      userComponents: []
    }
  }

  componentDidMount() {
    fetch('http://localhost:8080/components', { // TODO: Get only users component
      headers: new Headers({
        'Authorization': 'Bearer ' + Cookies.get('access_token'),
      }),
    })
      .then(resp => resp.json())
      .then(json => {
        this.setState({userComponents: json['_embedded']['components']});
      });
    new Chart(this.node, {
      type: "pie",
      options: {
        events: []
      },
      data: {
        labels: ["...", "...", "..."],
        datasets: [
          {
            data: [12, 19, 3]
          }
        ]
      }
    });
  }

  render() {
    return (
      <Container>
        <Row>
          {this.state.userComponents.map((data, i) => {
            return (<DataComponent comp={data} key={i}/>)
          })}
          <Col lg='6'>
            <Card>
              <CardBody>
                <Row>
                  <Col>
                    <CardTitle>Keyword</CardTitle>
                    <CardSubtitle>From ... to ...</CardSubtitle>
                  </Col>
                  <Col className={'text-right'}>
                    <a href={'# '}><span>Criar novo componente</span></a>
                  </Col>
                </Row>
              </CardBody>
              <canvas id={'canvasA'}
                      style={{width: 538, height: 283}}
                      ref={node => (this.node = node)}
              />
            </Card>
          </Col>
        </Row>
      </Container>
    );
  }

}

export default Dashboard;
