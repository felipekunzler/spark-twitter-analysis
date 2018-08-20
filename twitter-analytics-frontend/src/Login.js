import React, {Component} from 'react';
import Cookies from 'js-cookie';
import {Alert, Button, Col, Container, Form, FormGroup, Input, Jumbotron, Label, Row} from "reactstrap";

class Login extends Component {

  constructor() {
    super();
    this.state = {
      loginId: '',
      loginPassword: '',
      loginError: null,
      signupId: '',
      signupPassword: '',
      signupConfirmPassword: '',
      signupError: null
    }
  }

  handleLogin(e) {
    e.preventDefault();
    fetch('http://localhost:8080/oauth/token', {
      method: 'post',
      headers: new Headers({
        'Authorization': 'Basic d2ViYXBwOnNlY3JldA==',
        'Content-Type': 'application/x-www-form-urlencoded'
      }),
      body: 'grant_type=password&username=' + this.state.loginId + '&password=' + this.state.loginPassword
    })
      .then(res => {
        if (res.ok) {
          if (!res.ok) throw new Error(res.status);
          else return res.json();
        }
      })
      .then(res => {
        let expiryDate = new Date(new Date().getTime() + res['expires_in'] * 1000);
        Cookies.set('access_token', res['access_token'], {expires: expiryDate});
        this.props.setCurrentUser(this.state.loginId);
        this.props.history.push('/');
      })
      .catch(() => this.setState({loginError: 'Credenciais inválidas.'}));
  }

  handleSignup() {
    fetch('http://localhost:8080/users', {
      method: 'post',
      headers: new Headers({
        'Content-Type': 'application/json'
      }),
      body: { //test register
        "id": "user2",
        "password": "pass",
        "name": "User name"
      }
    })
      .then(res => res.json())
      .then(res => {
        let expiryDate = new Date(new Date().getTime() + res['expires_in'] * 1000);
        Cookies.set('access_token', res['access_token'], {expires: expiryDate});
        this.props.history.push('/');
      });
  }

  render() {
    return (
      <Container className={'loginPage'}>
        <Row>
          <Col>
            <Jumbotron>
              <h2 align="middle">Login</h2>
              {this.state.loginError ? <Alert color="danger">{this.state.loginError}</Alert> : null}
              <Form onSubmit={this.handleLogin.bind(this)}>
                <FormGroup>
                  <Label for="login-user">Usuário</Label>
                  <Input name="user" id="login-user" value={this.state.loginId}
                         onChange={e => this.setState({loginId: e.target.value})}/>
                </FormGroup>
                <FormGroup>
                  <Label for="login-password">Senha</Label>
                  <Input type="password" name="password" id="login-password" value={this.state.loginPassword}
                         onChange={e => this.setState({loginPassword: e.target.value})}/>
                </FormGroup>
                <Button block>Login</Button>
              </Form>
            </Jumbotron>
          </Col>
        </Row>
        <Row>
          <Col>
            <Jumbotron>
              <h2 align="middle">Sign up</h2>
              {this.state.signupError ? <Alert color="danger">{this.state.signupError}</Alert> : null}
              <Form>
                <FormGroup>
                  <Label for="signup-user">Usuário</Label>
                  <Input name="user" id="signup-user"/>
                </FormGroup>
                <FormGroup>
                  <Label for="signup-password">Senha</Label>
                  <Input type="password" name="password" id="signup-password"/>
                </FormGroup>
                <FormGroup>
                  <Label for="confirm-password">Confirmar Senha</Label>
                  <Input type="password" name="confirm-password" id="confirm-password"/>
                </FormGroup>
                <Button block onClick={this.handleSignup.bind(this)}>Sign up</Button>
              </Form>
            </Jumbotron>
          </Col>
        </Row>
      </Container>
    );
  }
}

export default Login;
