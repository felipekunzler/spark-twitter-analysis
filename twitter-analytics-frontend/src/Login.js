import React, {Component} from 'react';
import Cookies from 'js-cookie';

class Login extends Component {

  handleLogin() {
    fetch('http://localhost:8080/oauth/token', {
      method: 'post',
      headers: new Headers({
        'Authorization': 'Basic d2ViYXBwOnNlY3JldA==',
        'Content-Type': 'application/x-www-form-urlencoded'
      }),
      body: 'grant_type=password&username=admin&password=123'
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
      <div>
        <h1>login</h1>
        <input type="text"/><br/>
        <input type="password"/><br/>
        <button onClick={this.handleLogin.bind(this)}>login</button>
        <br/>
      </div>
    );
  }
}

export default Login;
