// Automatic FlutterFlow imports
import 'dart:collection';

import 'package:impilo/app_state.dart';
import 'package:impilo/main.dart';
import 'package:logging/logging.dart';

// Begin custom action code
// DO NOT REMOVE OR MODIFY THE CODE ABOVE!
final log = Logger('Regimens');

Future updateRegimenList() async {
  // Add your function code here!
  database.then((value) {
    FFAppState().regimens.clear();
    Set<String> regimens = new HashSet<String>();
    value.patientDao.listAssignedRegimen(FFAppState().code).then((ar) {
      ar.forEach((element) {
        regimens.add(element.assignedRegimen);
      });
      FFAppState().regimens = regimens.toList();
    });
  });
}
