import 'package:flutter/material.dart';
import 'package:impilo/app_state.dart';
import 'package:impilo/backend/floor/entities/inventory.dart';
import 'package:impilo/backend/floor/entities/inventory_request.dart';
import 'package:impilo/backend/floor/entities/patient.dart';
import 'package:impilo/backend/floor/entities/refill.dart';
import 'package:impilo/main.dart';
import 'package:oktoast/oktoast.dart';

import 'api.dart';

class AccountService {
  Future<void> processAccount() async {
    final response =
        await api.get('${FFAppState().baseUrl}/api/impilo/activation/activate');
    final data = await response.data;
    final _database = await database;
    final patients = data['patients'];
    await _database.patientDao.deleteAll();
    patients.forEach((p) async {
      await _database.patientDao.insertRecord(Patient.fromJson(p));
    });

    final refills = data['refills'];
    await _database.refillDao.deleteAll();
    refills.forEach((p) async {
      await _database.refillDao.insertRecord(Refill.fromJson(p));
    });

    final requests = data['requests'];
    await _database.inventoryRequestDao.deleteAll();
    requests.forEach((request) async {
      await _database.inventoryRequestDao
          .insertRecord(InventoryRequest.fromJson(request));
    });

    final stockIssues = data['stockIssues'];
    await _database.inventoryDao.deleteAll();
    if (stockIssues != null) {
      stockIssues.forEach((inventory) async {
        var curr = await _database.inventoryDao.findByUniqueIdAndRegimen(
            inventory['requestReference'], inventory['regimen']);
        var exists = curr
            .where((element) => element.reference == inventory['reference'])
            .isNotEmpty;
        if (!exists) {
          var _inventory = Inventory(
              null,
              inventory['requestReference'],
              inventory['reference'],
              inventory['regimen'],
              inventory['bottles'],
              inventory['balance'],
              inventory['acknowledged'] ?? false,
              inventory['batchNo'],
              inventory['barcode'],
              FFAppState().code,
              DateTime.parse(inventory['expirationDate']),
              inventory['batchIssueId'] ?? '');
          await _database.inventoryDao.insertRecord(_inventory);
          await _database.inventoryRequestDao
              .fulfilled(inventory['bottles'], inventory['requestReference']);
        }
      });
    }

    await _database.clinicDao.deleteAll();

    showToast(
      'Data from server successfully processed',
      duration: Duration(seconds: 10),
      position: ToastPosition.bottom,
      backgroundColor: Colors.green,
      radius: 3.0,
      textStyle: TextStyle(fontSize: 15.0),
    );
  }
}
