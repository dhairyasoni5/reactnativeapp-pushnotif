const { GoogleAuth } = require('google-auth-library');
const fetch = require('node-fetch');
const path = require('path');

// Configuration
const serviceAccountPath = path.join(__dirname, 'vedasdemo-75ce3ff72490.json');
const projectId = 'vedasdemo';
const deviceToken = 'c1mnNw1XRXatBwCiXkUlX_:APA91bHS3a5beRhYf5BA0nM9enY2rHc2zbBF9dIvl31mMSqrDlj3j-vvXpmuOX_SlDYS6OxXbFSBFPPgn2s4O2LI9bhx3Rv2HtiJD-bG4n_Aa0F14X5q2gM'; // FCM token from your app

async function getAccessToken() {
  const auth = new GoogleAuth({
    keyFile: serviceAccountPath,
    scopes: ['https://www.googleapis.com/auth/firebase.messaging'],
  });
  const client = await auth.getClient();
  const accessToken = await client.getAccessToken();
  return accessToken.token;
}

async function sendFcmMessage() {
  const accessToken = await getAccessToken();

  const message = {
    message: {
      token: deviceToken,
      data: {
        title: 'Incoming Astro-Consultation',
        callerName: 'Pandit Dhairyaji',
        callType: 'ASTRO_CONSULTATION',
        callTypeText: 'Incoming Audio Call',
        profileImageUrl: 'https://randomuser.me/api/portraits/men/44.jpg',
      },
    },
  };

  const response = await fetch(
    `https://fcm.googleapis.com/v1/projects/${projectId}/messages:send`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(message),
    }
  );

  const data = await response.json();
  console.log('FCM Response:', data);
}

sendFcmMessage().catch(console.error);