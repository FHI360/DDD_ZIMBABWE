// dao/person_dao.dart

import 'package:floor/floor.dart';

import '../entities/clinic_data.dart';

@dao
abstract class ClinicDao {
  @Query('SELECT * FROM Clinic')
  Future<List<ClinicData>> findAll();

  @Query('SELECT * FROM Clinic where synced = false')
  Future<List<ClinicData>> findUnSynced();

  @Query('SELECT * FROM Clinic WHERE id = :id')
  Stream<ClinicData?> findById(int id);

  @Query('SELECT * FROM Clinic WHERE patientId = :patientId')
  Future<List<ClinicData>> findByPatient(int patientId);

  @insert
  Future<void> insertRecord(ClinicData clinic);

  @Query("delete from Clinic where id = :id")
  Future<void> deleteById(int id);
}
