// Automatic FlutterFlow imports
import 'package:impilo/backend/floor/entities/inventory.dart';

import '/flutter_flow/flutter_flow_util.dart';
import '../../main.dart';

// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

Future<Inventory?> getNonZeroInventory(String regimen) async {
  // Add your function code here!
  var _database = await database;
  return _database.inventoryDao.getNonZeroInventory(FFAppState().code, regimen);
}
