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
    fetch('http://localhost:8080/components', {
      headers: new Headers({
        'Authorization': 'Bearer ' + Cookies.get('access_token'),
      }),
    })
      .then(resp => resp.json())
      .then(json => {
        this.setState({userComponents: json['_embedded']['components']});
      });
  }

  render() {
    return (
      <div>
        <div>Dashboard page here</div>
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
