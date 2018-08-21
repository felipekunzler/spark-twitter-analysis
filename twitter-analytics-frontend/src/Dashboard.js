import React, {Component} from 'react';
import Cookies from 'js-cookie';
import {Button, Card, CardBody, CardImg, CardSubtitle, CardText, CardTitle, Col, Container, Row} from "reactstrap";
import KeywordComponent from "./KeywordComponent";
import Chart from "chart.js";

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
        console.log(this.state.userComponents);
      });
    new Chart(this.node, {
      type: "pie",
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
            return (<KeywordComponent comp={data} key={i}/>)
          })}
          <Col lg='6'>
            <Card>
              <CardBody>
                <CardTitle>Keyword</CardTitle>
                <CardSubtitle>From ... to ...</CardSubtitle>
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
