import 'package:flutter/material.dart';
import 'package:impilo/app_state.dart';
import 'package:impilo/backend/floor/entities/clinic_data.dart';
import 'package:impilo/backend/floor/entities/devolve.dart';
import 'package:impilo/backend/floor/entities/inventory_request.dart';
import 'package:impilo/backend/floor/entities/refill.dart';
import 'package:oktoast/oktoast.dart';

import '../../main.dart';
import 'api.dart';

class SyncService {
  Future<bool?> _syncRecords() async {
    final _database = await database;
    List<Refill> dispenses = await _database.refillDao.findUnSynced();
    List<ClinicData> clinic = await _database.clinicDao.findUnSynced();
    List<Devolve> devolves = await _database.devolveDao.findUnSynced();
    List<InventoryRequest> requests =
        await _database.inventoryRequestDao.findUnSynced();
    List<InventoryRequest> newRequest =
        requests.where((e) => e.acknowledged == false).toList();
    List<InventoryRequest> acknowledgements =
        requests.where((e) => e.acknowledged == true).toList();

    final Map<String, dynamic> payload = new Map<String, dynamic>();
    payload['refills'] = dispenses.map((d) => d.toJson()).toList();
    payload['clinicData'] = clinic.map((c) => c.toJson()).toList();
    payload['devolves'] = devolves.map((e) => e.toJson()).toList();
    payload['requests'] = newRequest.map((e) => e.toJson()).toList();
    payload['acknowledgements'] = acknowledgements
        .map((e) => {
              {
                'organisation': {'id': FFAppState().code},
                'uniqueId': e.uniqueId
              }
            })
        .toList();

    final response = await api.post(
      '${FFAppState().baseUrl}/api/impilo/mobile-sync',
      data: payload,
    );

    await _database.clinicDao.updateAllSynced();
    await _database.devolveDao.updateAllSynced();
    await _database.refillDao.updateAllSynced();
    await _database.inventoryRequestDao.updateAllSynced();
    var start = DateTime.now().subtract(Duration(days: 366));

    await _database.refillDao.deleteOlderThan(start);
    await _database.devolveDao.deleteAll();
    await _database.clinicDao.deleteAll();

    return response.data;
  }

  Future<bool> processSync() async {
    final _database = await database;
    var hasData = await _database.clinicDao.hasUnSynced();
    if (!(hasData ?? false)) {
      hasData = await _database.refillDao.hasUnSynced();
    }
    if (!(hasData ?? false)) {
      hasData = await _database.devolveDao.hasUnSynced();
    }
    if (!(hasData ?? false)) {
      hasData = await _database.inventoryRequestDao.hasUnSynced();
    }
    if (hasData ?? false) {
      try {
        final response = await _syncRecords();
        if (response != null && response) {
          showToast(
            'Data successfully synchronized',
            duration: Duration(seconds: 10),
            position: ToastPosition.bottom,
            backgroundColor: Colors.green,
            radius: 3.0,
            textStyle: TextStyle(fontSize: 15.0),
          );
        } else {
          showToast(
            'There was error synchronizing with server',
            duration: Duration(seconds: 10),
            position: ToastPosition.bottom,
            backgroundColor: Colors.red,
            radius: 3.0,
            textStyle: TextStyle(fontSize: 15.0),
          );

          return false;
        }
      } catch (e) {
        if (e.toString().contains('Connection refused')) {
          showToast(
            'There is a connection error',
            duration: Duration(seconds: 10),
            position: ToastPosition.bottom,
            backgroundColor: Colors.red,
            radius: 3.0,
            textStyle: TextStyle(fontSize: 15.0),
          );
        }
        return false;
      }
    }

    return true;
  }
}
