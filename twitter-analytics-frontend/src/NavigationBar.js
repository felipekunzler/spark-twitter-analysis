import React from 'react';
import {
  Collapse,
  Navbar,
  NavbarToggler,
  NavbarBrand,
  Nav,
  NavItem,
  NavLink
} from 'reactstrap';
import Cookies from "js-cookie";
import {withRouter} from 'react-router-dom'

class NavigationBar extends React.Component {
  constructor(props) {
    super(props);

    this.toggle = this.toggle.bind(this);
    this.state = {
      isOpen: false
    };
  }

  toggle() {
    this.setState({
      isOpen: !this.state.isOpen
    });
  }

  handleSignOut() {
    Cookies.remove('access_token');
    this.props.setCurrentUser(null);
    this.props.history.push('/');
  }

  render() {
    return (
      <div>
        <Navbar color='light' expand={true}>
          <NavbarBrand href="/" className={'current-user'}>Twitter Analytics</NavbarBrand>
          <NavbarToggler onClick={this.toggle}/>
          <Collapse isOpen={this.state.isOpen} navbar>
            <Nav className="ml-auto" navbar style={Cookies.get('access_token') ? {} : {display: 'none'}}>
              <NavItem>
                <NavLink>Bem-vindo <span className={'current-user'}>{this.props.currentUser}</span>!</NavLink>
              </NavItem>
              <NavItem>
                <NavLink href='#' onClick={this.handleSignOut.bind(this)}>Sign out</NavLink>
              </NavItem>
            </Nav>
          </Collapse>
        </Navbar>
      </div>
    );
  }
}

export default withRouter(NavigationBar)
