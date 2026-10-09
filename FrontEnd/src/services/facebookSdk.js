import { FACEBOOK_APP_ID } from '../configs/constants';

const SDK_URL = 'https://connect.facebook.net/en_US/sdk.js';
const GRAPH_VERSION = 'v23.0';

let sdkPromise = null;

// Loads and initialises the Facebook JS SDK once; later calls reuse the same promise.
export function loadFacebookSdk() {
  if (sdkPromise) return sdkPromise;

  sdkPromise = new Promise((resolve, reject) => {
    if (!FACEBOOK_APP_ID) {
      reject(new Error('VITE_FACEBOOK_APP_ID is not configured'));
      return;
    }
    if (window.FB) {
      resolve(window.FB);
      return;
    }

    window.fbAsyncInit = () => {
      // cookie: false — the backend issues our own session cookies; no Facebook session cookie is needed
      window.FB.init({ appId: FACEBOOK_APP_ID, cookie: false, xfbml: false, version: GRAPH_VERSION });
      resolve(window.FB);
    };

    const script = document.createElement('script');
    script.src = SDK_URL;
    script.async = true;
    script.defer = true;
    script.crossOrigin = 'anonymous';
    script.onerror = () => {
      sdkPromise = null; // allow a retry (e.g. blocked by an ad blocker, then unblocked)
      script.remove();
      reject(new Error('Could not load the Facebook SDK'));
    };
    document.body.appendChild(script);
  });

  return sdkPromise;
}

// Opens the Facebook login popup and resolves with the user access token,
// or null when the user closes the popup / denies permission.
// Must be called directly from a click handler (with the SDK already loaded) so the popup is not blocked.
export function facebookLogin(FB) {
  return new Promise((resolve) => {
    FB.login(
      (response) => resolve(response?.status === 'connected' ? response.authResponse.accessToken : null),
      // auth_type=rerequest asks again for email if the user declined it the first time
      { scope: 'public_profile,email', auth_type: 'rerequest' }
    );
  });
}
