const express = require('express');
const admin = require('firebase-admin');
const path = require('path');

const app = express();
app.use(express.json());

// Initialize Firebase Admin SDK
const serviceAccount = require('./vedasdemo-75ce3ff72490.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

// API key authentication middleware
const API_KEY = "demoapikey123";
app.use((req, res, next) => {
  const key = req.headers['x-api-key'];
  if (key !== API_KEY) {
    return res.status(401).json({ error: "Unauthorized - Invalid API Key" });
  }
  next();
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'OK', message: 'Backend simulation server is running' });
});

// POST /simulate-call endpoint
app.post('/simulate-call', async (req, res) => {
  const { deviceToken, callerName = 'Pandit Dhairyaji' } = req.body;

  if (!deviceToken) {
    return res.status(400).json({ error: "deviceToken is required" });
  }

  const message = {
    token: deviceToken,
    data: {
      title: 'Incoming Astro-Consultation',
      callerName: callerName,
      callType: 'ASTRO_CONSULTATION',
      callTypeText: 'Incoming Audio Call',
      profileImageUrl: 'https://randomuser.me/api/portraits/men/44.jpg',
    },
  };

  try {
    const response = await admin.messaging().send(message);
    console.log('✅ Successfully sent call notification:', response);
    res.json({ 
      success: true, 
      messageId: response,
      message: `Call notification sent to ${callerName}`,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    console.error('❌ Error sending message:', error);
    res.status(500).json({ 
      error: 'Failed to send message',
      details: error.message 
    });
  }
});

app.get('/simulate-call', (req, res) => {
  const { deviceToken, callerName = 'Pandit Dhairyaji' } = req.query;
  
  if (!deviceToken) {
    return res.status(400).json({ 
      error: "deviceToken is required as query parameter",
      example: "/simulate-call?deviceToken=YOUR_TOKEN&callerName=Astrologer"
    });
  }

  // Redirect to POST with the same parameters
  res.json({
    message: "Use POST /simulate-call with deviceToken in body",
    example: {
      method: "POST",
      url: "/simulate-call",
      headers: {
        "Content-Type": "application/json",
        "x-api-key": "demoapikey123"
      },
      body: {
        deviceToken: deviceToken,
        callerName: callerName
      }
    }
  });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log('🚀 Backend simulation server running on http://localhost:' + PORT);
  console.log('📱 Health check: GET http://localhost:' + PORT + '/health');
  console.log('📞 Simulate call: POST http://localhost:' + PORT + '/simulate-call');
  console.log('🔑 API Key: demoapikey123');
}); 