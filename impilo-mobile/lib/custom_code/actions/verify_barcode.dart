// Automatic FlutterFlow imports
import 'package:flutter/material.dart';
import 'package:oktoast/oktoast.dart';

import '/flutter_flow/flutter_flow_util.dart';
import '../../main.dart';

// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

Future<bool> verifyBarcode(String barcode, String regimen, int bottles) async {
  // Add your function code here!
  final _database = await database;

  final inventory = await _database.inventoryDao
      .barcodeQuantity(FFAppState().code, regimen, barcode);
  int total = 0;
  if (inventory != null) {
    total = inventory.quantity;
  }
  if (total == 0) {
    showToast(
      '''No medication with provided code and ARV Drug '$regimen' found''',
      duration: Duration(seconds: 2),
      position: ToastPosition.bottom,
      backgroundColor: Colors.red,
      radius: 3.0,
      textStyle: TextStyle(fontSize: 15.0),
    );

    return Future.value(false);
  }
  int used = 0;
  final refill = await _database.refillDao
      .barcodeQuantity(FFAppState().code, regimen, barcode);
  if (refill != null) {
    used = refill.quantity;
  }
  if (total < (used + bottles)) {
    showToast(
      '''Not enough quantity to dispense for code and ARV Drug '$regimen' ''',
      duration: Duration(seconds: 2),
      position: ToastPosition.bottom,
      backgroundColor: Colors.red,
      radius: 3.0,
      textStyle: TextStyle(fontSize: 15.0),
    );

    return Future.value(false);
  }
  return Future.value(true);
}
