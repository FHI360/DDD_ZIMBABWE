import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/foundation.dart';

Future initFirebase() async {
  if (kIsWeb) {
    await Firebase.initializeApp(
        options: FirebaseOptions(
            apiKey: "AIzaSyDMv-gFVEF-GLxuLO3-17d2tf5dYAD2sLE",
            authDomain: "impilo-f190d.firebaseapp.com",
            projectId: "impilo-f190d",
            storageBucket: "impilo-f190d.appspot.com",
            messagingSenderId: "839507105879",
            appId: "1:839507105879:web:428fb15bd2d810c5a5eb12",
            measurementId: "G-SBZPRNBXF2"));
  } else {
    await Firebase.initializeApp();
  }
}
