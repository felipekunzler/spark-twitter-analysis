import React, {Component} from 'react';
import Cookies from 'js-cookie';

class Dashboard extends Component {

  constructor() {
    super();
    this.state = {
      userComponents: []
    }
  }

  componentDidMount() {
    let token = Cookies.get('access_token');
    fetch('http://localhost:8080/components', {
      headers: new Headers({
        'Authorization': 'Bearer ' + token,
      }),
    })
      .then(resp => resp.json())
      .then(json => {
        this.setState({userComponents: json['_embedded']['components']});
      });
  }

  handleSignOut() {
    Cookies.remove('access_token');
    this.props.history.push('/');
  }

  render() {
    return (
      <div>
        <div>Dashboard page here</div>
        <button onClick={this.handleSignOut.bind(this)}>Sign out</button>
        <div>
          {this.state.userComponents.map((d, idx) => {
            return (<li key={idx}>{d.name}</li>)
          })}
        </div>
      </div>
    );
  }

}

export default Dashboard;
