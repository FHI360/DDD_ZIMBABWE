import 'package:flutter/material.dart';
import 'package:impilo/app_state.dart';
import 'package:impilo/backend/floor/entities/patient.dart';
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
