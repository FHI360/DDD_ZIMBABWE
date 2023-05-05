// Automatic FlutterFlow imports
import 'package:impilo/main.dart';

import '/flutter_flow/flutter_flow_util.dart';
// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

Future updateStockAvailability(String regimen) async {
  // Add your function code here!
  FFAppState().regimenQty = 0;
  database.then((value) {
    int total = 0;
    value.inventoryDao
        .selectInventoryQuantity(FFAppState().code, regimen)
        .then((qty) {
      qty.forEach((element) {
        total += element.quantity;
      });
      FFAppState().regimenQty = total.toInt();
    });
  });
}
