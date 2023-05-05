// Automatic FlutterFlow imports
import '/backend/backend.dart';
import '/flutter_flow/flutter_flow_theme.dart';
import '/flutter_flow/flutter_flow_util.dart';
import 'index.dart'; // Imports other custom actions
import '/flutter_flow/custom_functions.dart'; // Imports custom functions
import 'package:flutter/material.dart';
// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

// DO NOT REMOVE OR MODIFY THE CODE ABOVE!return Future.value(true);

Future<bool> validatePasswords(
  String password,
  String confirm,
) async {
  // Add your function code here!
  if (password == confirm) {
    return Future.value(true);
  }
  return Future.value(false);
}
