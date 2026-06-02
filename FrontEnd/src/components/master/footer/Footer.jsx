import React from 'react';
import './Footer.css';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEnvelope, faPhone } from '@fortawesome/free-solid-svg-icons';
import { faFacebook, faInstagram, faLinkedin } from '@fortawesome/free-brands-svg-icons';

export default function Footer() {
    return (
        <footer className="footer">
            <div className="footer-container">

                {/* Brand */}
                <div className="footer-section">
                    <h2 className="footer-logo">My Store</h2>
                    <p className="footer-description">
                        Your one-stop shop for all your needs.
                    </p>
                </div>

                {/* Support */}
                <div className="footer-section">
                    <h3>Support</h3>
                    <ul>
                        <li>Help Center</li>
                        <li>Return Policy</li>
                        <li>Shipping Info</li>
                        <li>Security</li>
                    </ul>
                </div>

                {/* Contact */}
                <div className="footer-section">
                    <h3>Contact</h3>

                    <p>
                        <FontAwesomeIcon icon={faEnvelope} className="icon" />
                        support@ecom.com
                    </p>

                    <p>
                        <FontAwesomeIcon icon={faPhone} className="icon" />
                        0123 456 789
                    </p>

                    <div className="footer-socials">
                        <span><FontAwesomeIcon icon={faFacebook} /></span>
                        <span><FontAwesomeIcon icon={faInstagram} /></span>
                        <span><FontAwesomeIcon icon={faLinkedin} /></span>
                    </div>
                </div>

            </div>

            <div className="footer-bottom">
                © {new Date().getFullYear()} E-Com. All rights reserved.
            </div>
        </footer>
    );
}