import { useNavigate } from 'react-router-dom';
import './OnboardingPage.css';

const LEFT_FEATURES = [
  { icon: '🚀', title: 'Fast Delivery', desc: 'Same-day shipping to your door' },
  { icon: '🔒', title: 'Secure Payments', desc: 'End-to-end encrypted checkout' },
  { icon: '↩️', title: 'Easy Returns', desc: '30-day hassle-free returns' },
  { icon: '🎁', title: 'Exclusive Deals', desc: 'Members-only offers every week' },
];

const RIGHT_FEATURES = [
  { icon: '📦', title: '10M+ Products', desc: 'Across hundreds of categories' },
  { icon: '⭐', title: 'Trusted Sellers', desc: 'Verified vendors, real reviews' },
  { icon: '💬', title: '24/7 Support', desc: 'Live chat whenever you need' },
  { icon: '🌍', title: 'Ship Worldwide', desc: 'Delivering to 50+ countries' },
];

export default function OnboardingPage() {
  const navigate = useNavigate();

  return (
    <div className="onboarding-wrapper">

      {/* Left panel */}
      <aside className="onboarding-side onboarding-side--left">
        <div className="onboarding-side-brand">
          <span className="onboarding-side-logo">🛍️</span>
          <span className="onboarding-side-name">TnCart</span>
        </div>
        <p className="onboarding-side-tagline">Everything you need,<br />all in one place.</p>
        <ul className="onboarding-features">
          {LEFT_FEATURES.map((f) => (
            <li key={f.title} className="onboarding-feature">
              <span className="onboarding-feature-icon">{f.icon}</span>
              <span className="onboarding-feature-body">
                <strong>{f.title}</strong>
                <span>{f.desc}</span>
              </span>
            </li>
          ))}
        </ul>
      </aside>

      {/* Center card */}
      <main className="onboarding-card">
        <p className="onboarding-platform">Welcome to TnCart</p>
        <h1 className="onboarding-title">You're all set! 🎉</h1>
        <p className="onboarding-subtitle">
          Let's personalise your experience.<br />You can always change this later.
        </p>

        <p className="onboarding-question">What would you like to do?</p>

        <div className="onboarding-options">
          <button
            className="onboarding-option onboarding-option--seller"
            onClick={() => navigate('/vendor-create')}
          >
            <span className="onboarding-option-icon">🛍</span>
            <span className="onboarding-option-body">
              <span className="onboarding-option-title">Start selling products</span>
              <span className="onboarding-option-desc">
                Set up your vendor store and list your first product
              </span>
            </span>
            <span className="onboarding-option-arrow">›</span>
          </button>

          <button
            className="onboarding-option onboarding-option--customer"
            onClick={() => navigate('/home')}
          >
            <span className="onboarding-option-icon">🛒</span>
            <span className="onboarding-option-body">
              <span className="onboarding-option-title">Just continue as a customer</span>
              <span className="onboarding-option-desc">
                Browse and shop now — you can become a vendor later
              </span>
            </span>
            <span className="onboarding-option-arrow">›</span>
          </button>
        </div>
      </main>

      {/* Right panel */}
      <aside className="onboarding-side onboarding-side--right">
        <p className="onboarding-side-tagline">Join millions of<br />happy shoppers.</p>
        <ul className="onboarding-features">
          {RIGHT_FEATURES.map((f) => (
            <li key={f.title} className="onboarding-feature">
              <span className="onboarding-feature-icon">{f.icon}</span>
              <span className="onboarding-feature-body">
                <strong>{f.title}</strong>
                <span>{f.desc}</span>
              </span>
            </li>
          ))}
        </ul>
        <div className="onboarding-side-stats">
          <div className="onboarding-stat">
            <strong>2M+</strong>
            <span>Sellers</span>
          </div>
          <div className="onboarding-stat">
            <strong>50M+</strong>
            <span>Customers</span>
          </div>
          <div className="onboarding-stat">
            <strong>4.9★</strong>
            <span>Rating</span>
          </div>
        </div>
      </aside>

    </div>
  );
}
