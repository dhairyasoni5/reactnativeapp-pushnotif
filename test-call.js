const fetch = require('node-fetch');

// Configuration
const SERVER_URL = 'http://localhost:3000';
const API_KEY = 'demoapikey123';
const DEVICE_TOKEN = 'c1mnNw1XRXatBwCiXkUlX_:APA91bHS3a5beRhYf5BA0nM9enY2rHc2zbBF9dIvl31mMSqrDlj3j-vvXpmuOX_SlDYS6OxXbFSBFPPgn2s4O2LI9bhx3Rv2HtiJD-bG4n_Aa0F14X5q2gM'; // Replace with your actual token

async function testHealth() {
  try {
    console.log('🏥 Testing health endpoint...');
    const response = await fetch(`${SERVER_URL}/health`);
    const data = await response.json();
    console.log('✅ Health check response:', data);
    return true;
  } catch (error) {
    console.error('❌ Health check failed:', error.message);
    return false;
  }
}

async function testSimulateCall() {
  try {
    console.log('📞 Testing simulate call endpoint...');
    const response = await fetch(`${SERVER_URL}/simulate-call`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'x-api-key': API_KEY
      },
      body: JSON.stringify({
        deviceToken: DEVICE_TOKEN,
        callerName: 'Pandit Dhairyaji'
      })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      console.log('✅ Call simulation successful:', data);
    } else {
      console.error('❌ Call simulation failed:', data);
    }
    
    return response.ok;
  } catch (error) {
    console.error('❌ Call simulation error:', error.message);
    return false;
  }
}

async function runTests() {
  console.log('🚀 Starting backend tests...\n');
  
  // Test health endpoint
  const healthOk = await testHealth();
  if (!healthOk) {
    console.log('❌ Server is not running. Please start the server first:');
    console.log('   npm install');
    console.log('   node server.js');
    return;
  }
  
  console.log('\n' + '='.repeat(50) + '\n');
  
  // Test simulate call
  await testSimulateCall();
  
  console.log('\n' + '='.repeat(50));
  console.log('🎯 Test completed! Check your mobile app for the notification.');
}

// Run tests
runTests().catch(console.error); 