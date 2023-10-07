import 'package:flutter/services.dart';
import 'package:impilo/backend/floor/dao/refill_dao.dart';
import 'package:impilo/custom_code/init_cap.dart';

import '/flutter_flow/flutter_flow_util.dart';
import '../main.dart';

Future<String> refillInfoPdf(DateTime start, DateTime end) async {
  var _database = await database;
  List<RefillInfo> rows =
      await _database.refillDao.listRefillInfo(FFAppState().code, start, end);
  String style = await rootBundle.loadString('assets/css/bootstrap.min.css');
  String content = '''
  <div class="row">
    <div class="col-12 justify-content-center m-3">
      <h1 class="text-center">${FFAppState().name}</h1>
    </div>
  </div>
  <div class="row">
    <div class="col-12 justify-content-center mb-3">
      <h2 class="text-center">Refill Appointments between ${dateTimeFormat('yMMMd', start)} and ${dateTimeFormat('yMMMd', end)}</h2>
    </div>
  </div>
  <div class="row">
    <div class="col-12">
      <table class="table table-sm table-striped">
        <thead class="table-light">
        <tr>
            <th scope="col" colspan="1" class="text-center">#</th>
            <th scope="col" colspan="4" class="text-center">Patient</th>
            <th scope="col" colspan="3" class="text-center">Hospital No.</th>
            <th scope="col" colspan="2" class="text-center">Date of Birth</th>
            <th scope="col" class="text-center">Sex</th>
            <th scope="col" colspan="4" class="text-center">Regimen</th>
            <th scope="col" class="text-center">Qty (bottle(s))</th>
            <th scope="col" colspan="2" class="text-center">Last Refill</th>
            <th scope="col" colspan="2" class="text-center">Next Appointment</th>
        </tr>
        </thead>   
        <tbody>
  ''';
  rows.mapIndexed((i, e) {
    content += '''
      <tr>
        <th scope="row">${i + 1}</th>
        <td class="justify-content-start" colspan="4">${e.givenName} ${e.familyName}</td>
        <td class="justify-content-start" colspan="3">${e.hospitalNo}</td>
        <td colspan="2">
          <span class="d-flex justify-content-end">
            ${dateTimeFormat('yMMMd', e.dateOfBirth)}
          </span>
        </td>
        <td class="justify-content-start">${capitalizeFirstLetter(e.sex)}</td>
        <td class="justify-content-start" colspan="4">${e.regimen}</td>
        <td>
          <span class="d-flex justify-content-end">
            ${formatNumber(e.quantity,formatType: FormatType.custom,format: '#,##0')}
          </span>
        </td>
        <td colspan="2">
          <span class="d-flex justify-content-end">
            ${dateTimeFormat('yMMMd', e.date)}
          </span>
        </td>
        <td colspan="2">
          <span class="d-flex justify-content-end">
            ${dateTimeFormat('yMMMd', e.dateNextRefill)}
          </span>
        </td>
      </tr>
    ''';
  });

  content += '</tbody></table></div></div>';

  String html = '''
  <!DOCTYPE html>
  <html lang="en">
    <style>
      $style
    </style>
    <body>
      <div class="container">
        $content
      </div>
    </body>
  </html>
  ''';
  return html;
}
