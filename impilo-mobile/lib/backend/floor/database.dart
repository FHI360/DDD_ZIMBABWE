// database.dart

import 'dart:async';

import 'package:floor/floor.dart';
import 'package:impilo/backend/floor/dao/clinic_dao.dart';
import 'package:impilo/backend/floor/dao/inventory_dao.dart';
import 'package:impilo/backend/floor/dao/patient_dao.dart';
import 'package:impilo/backend/floor/entities/devolve.dart';
import 'package:impilo/backend/floor/entities/inventory.dart';
import 'package:impilo/backend/floor/entities/inventory_request.dart';
import 'package:sqflite/sqflite.dart' as sqflite;

import 'dao/inventory_request_dao.dart';
import 'dao/refill_dao.dart';
import 'date-time-converter.dart';
import 'entities/clinic_data.dart';
import 'entities/patient.dart';
import 'entities/refill.dart';

part 'database.g.dart'; // the generated code will be there

@TypeConverters([DateTimeConverter])
@Database(version: 2, entities: [
  ClinicData,
  Patient,
  Refill,
  Inventory,
  InventoryRequest,
  Devolve
], views: [
  AssignedRegimen,
  BarcodeDispense,
  BarcodeQuantity,
  InventoryQuantity,
  LastRefill,
  InventoryAvailability,
  EstimatedRefill,
  RefillInfo,
  MissedRefill
])
abstract class AppDatabase extends FloorDatabase {
  ClinicDao get clinicDao;

  PatientDao get patientDao;

  RefillDao get refillDao;

  InventoryDao get inventoryDao;

  InventoryRequestDao get inventoryRequestDao;

  DevolveDao get devolveDao;
}
