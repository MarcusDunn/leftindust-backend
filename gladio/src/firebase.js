const firebase = require("firebase/app");
require("firebase/auth");

const firebaseConfig = {
    apiKey: "AIzaSyDwHAnFFB6ouYkL068TmFubVpa8Mlp8UBE",
    authDomain: "mediq-backend.firebaseapp.com",
    databaseURL: "https://mediq-backend.firebaseio.com",
    projectId: "mediq-backend",
    storageBucket: "mediq-backend.appspot.com",
    messagingSenderId: "710125939146",
    appId: "1:710125939146:web:b4d6cebab35d37d7f5c733"
};

firebase.initializeApp(firebaseConfig)

module.exports = {
    firebase
}