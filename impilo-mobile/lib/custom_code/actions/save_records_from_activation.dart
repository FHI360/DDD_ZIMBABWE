// Automatic FlutterFlow imports
import 'package:impilo/backend/floor/entities/refill.dart';
import 'package:impilo/main.dart';

import '/flutter_flow/flutter_flow_util.dart';
import '../../backend/floor/entities/patient.dart';

// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!

Future saveRecordsFromActivation(dynamic data, String siteCode) async {
  // Add your function code here!
  //Loop and create records
  var _database = await database;
  for (var i = 0; i < data.length; i++) {
    var patient = data[i];
    int? patientId;

    var _patient = await _database.patientDao.findByUniqueId(getJsonField(
      (patient ?? ''),
      r'''$.uniqueId''',
    ).toString());

    if (_patient != null) {
      _patient.sex = getJsonField(
        (patient ?? ''),
        r'''$.sex''',
      ).toString();
      _patient.givenName = getJsonField(
        (patient ?? ''),
        r'''$.givenName''',
      ).toString();
      _patient.familyName = getJsonField(
        (patient ?? ''),
        r'''$.familyName''',
      ).toString();
      _patient.dateOfBirth = DateTime.tryParse(getJsonField(
            (patient ?? ''),
            r'''$.dateOfBirth''',
          ).toString()) ??
          DateTime(1900);
      _patient.phoneNumber = getJsonField(
        (patient ?? ''),
        r'''$.phoneNumber''',
      ).toString();
      _patient.assignedRegimen = getJsonField(
        (patient ?? ''),
        r'''$.regimen''',
      ).toString();
      _patient.siteCode = siteCode;
      _patient.address = getJsonField(
        (patient ?? ''),
        r'''$.address''',
      ).toString();
      _patient.hospitalNo = getJsonField(
        (patient ?? ''),
        r'''$.hospitalNumber''',
      ).toString();
      _patient.lastClinicVisit = DateTime.tryParse(getJsonField(
            (patient ?? ''),
            r'''$.lastClinicVisit''',
          ).toString()) ??
          DateTime(1900);
      _patient.lastRefillDate = DateTime.tryParse(getJsonField(
            (patient ?? ''),
            r'''$.lastRefillDate''',
          ).toString()) ??
          DateTime(1900);
      _patient.nextTptDate = DateTime.tryParse(getJsonField(
            (patient ?? ''),
            r'''$.nextTptDate''',
          ).toString()) ??
          DateTime(1900);
      _patient.nextViralLoadDate = DateTime.tryParse(getJsonField(
            (patient ?? ''),
            r'''$.nextViralLoadDate''',
          ).toString()) ??
          DateTime(1900);
      _patient.nextCervicalCancerDate = DateTime.tryParse(getJsonField(
            (patient ?? ''),
            r'''$.nextCervicalCancerDate''',
          ).toString()) ??
          DateTime(1900);
      _patient.nextVisitDate = DateTime.tryParse(getJsonField(
            (patient ?? ''),
            r'''$.nextVisitDate''',
          ).toString()) ??
          DateTime(1900);
      _database.patientDao.updateRecord(_patient);
      patientId = _patient.id!;
    } else {
      _patient = Patient(
          null,
          getJsonField(
            (patient ?? ''),
            r'''$.givenName''',
          ).toString(),
          getJsonField(
            (patient ?? ''),
            r'''$.familyName''',
          ).toString(),
          getJsonField(
            (patient ?? ''),
            r'''$.hospitalNumber''',
          ).toString(),
          getJsonField(
            (patient ?? ''),
            r'''$.uniqueId''',
          ).toString(),
          DateTime.tryParse(getJsonField(
                (patient ?? ''),
                r'''$.dateOfBirth''',
              ).toString()) ??
              DateTime(1900),
          getJsonField(
            (patient ?? ''),
            r'''$.sex''',
          ).toString(),
          getJsonField(
            (patient ?? ''),
            r'''$.phoneNumber''',
          ).toString(),
          getJsonField(
            (patient ?? ''),
            r'''$.regimen''',
          ).toString(),
          getJsonField(
            (patient ?? ''),
            r'''$.facility''',
          ).toString(),
          siteCode,
          getJsonField(
            (patient ?? ''),
            r'''$.address''',
          ).toString(),
          DateTime.tryParse(getJsonField(
                (patient ?? ''),
                r'''$.lastClinicVisit''',
              ).toString()) ??
              DateTime(1900),
          DateTime.tryParse(getJsonField(
                (patient ?? ''),
                r'''$.lastRefillDate''',
              ).toString()) ??
              DateTime(1900),
          DateTime.tryParse(getJsonField(
                (patient ?? ''),
                r'''$.nextTptDate''',
              ).toString()) ??
              DateTime(1900),
          DateTime.tryParse(getJsonField(
                (patient ?? ''),
                r'''$.nextViralLoadDate''',
              ).toString()) ??
              DateTime(1900),
          DateTime.tryParse(getJsonField(
                (patient ?? ''),
                r'''$.nextCervicalCancerDate''',
              ).toString()) ??
              DateTime(1900),
          DateTime.tryParse(getJsonField(
                (patient ?? ''),
                r'''$.nextVisitDate''',
              ).toString()) ??
              DateTime(1900),
          false,
          '',
          DateTime(1900), true);

      patientId = await _database.patientDao.insertRecord(
        _patient,
      );
    }

    if (patientId != 0) {
      List<dynamic> refills = patient['refills'];
      for (var j = 0; j < refills.length; j++) {
        var refill = refills[j];
        var rfs = await _database.refillDao.findByPatientAndDate(
            patientId,
            DateTime.parse(getJsonField(
              (refill ?? ''),
              r'''$.date''',
            ).toString()));
        if (rfs.isNotEmpty) {
          var _refill = rfs.first;
          _refill.synced = true;
          _refill.regimen = getJsonField(
            (refill ?? ''),
            r'''$.regimen''',
          ).toString();
          _refill.dateNextRefill = DateTime.tryParse(getJsonField(
                (refill ?? ''),
                r'''$.dateNextRefill''',
              ).toString()) ??
              DateTime(1900);
          _refill.quantityDispensed = int.tryParse(getJsonField(
                (refill ?? ''),
                r'''$.qtyDispensed''',
              ).toString()) ??
              0;
          _refill.quantityPrescribed = int.tryParse(getJsonField(
                (refill ?? ''),
                r'''$.qtyPrescribed''',
              ).toString()) ??
              0;
          _database.refillDao.updateRecord(_refill);
        } else {
          var _refill = Refill(
              null,
              DateTime.tryParse(getJsonField(
                    (refill ?? ''),
                    r'''$.date''',
                  ).toString()) ??
                  DateTime(1900),
              getJsonField(
                (refill ?? ''),
                r'''$.regimen''',
              ).toString(),
              patientId,
              int.tryParse(getJsonField(
                    (refill ?? ''),
                    r'''$.qtyPrescribed''',
                  ).toString()) ??
                  0,
              int.tryParse(getJsonField(
                    (refill ?? ''),
                    r'''$.qtyDispensed''',
                  ).toString()) ??
                  0,
              DateTime.tryParse(getJsonField(
                    (refill ?? ''),
                    r'''$.dateNextRefill''',
                  ).toString()) ??
                  DateTime(1900),
              false,
              false,
              '',
              true);
          _database.refillDao.insertRecord(_refill);
        }
      }
    }
  }
}
