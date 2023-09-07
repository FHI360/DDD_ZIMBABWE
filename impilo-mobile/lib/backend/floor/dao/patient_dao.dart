import 'package:floor/floor.dart';
import 'package:impilo/backend/floor/entities/patient.dart';

@DatabaseView(
    'select distinct assignedRegimen, siteCode from patient where serviceDiscontinued = 0',
    viewName: 'AssignedRegimen')
class AssignedRegimen {
  final String assignedRegimen;
  final String siteCode;

  AssignedRegimen(this.assignedRegimen, this.siteCode);
}

@DatabaseView('''
  WITH last_refill AS ( 
      SELECT * FROM (
            SELECT patientId, date, dateNextRefill, ROW_NUMBER() OVER (PARTITION BY patientId 
            ORDER BY date DESC) rn FROM refill
      ) r WHERE rn = 1
  )
  SELECT siteCode, givenName, familyName, hospitalNo, sex, dateOfBirth, date, 
    dateNextRefill FROM last_refill JOIN patient ON patientId = uuid ORDER BY givenName, familyName
''', viewName: 'LastRefill')
class LastRefill {
  final String siteCode;
  final String givenName;
  final String familyName;
  final String sex;
  final DateTime dateOfBirth;
  final DateTime date;
  final DateTime dateNextRefill;
  final String hospitalNo;

  LastRefill(this.siteCode, this.givenName, this.familyName, this.sex,
      this.dateOfBirth, this.date, this.dateNextRefill, this.hospitalNo);
}

@dao
abstract class PatientDao {
  @Query(
      '''SELECT * FROM Patient where siteCode = :siteCode and serviceDiscontinued = 0
        order by givenName, familyName limit 10''')
  Future<List<Patient>> findAll(String siteCode);

  @Query(
      '''SELECT * FROM Patient where siteCode = :siteCode and serviceDiscontinued = 0 
      and (lower(givenName) like lower(:keyword) or lower(familyName) like 
      lower(:keyword) or lower(hospitalNo) like lower(:keyword)) order by givenName, 
      familyName limit 10''')
  Future<List<Patient>> findByKeyword(String siteCode, String keyword);

  @Query(
      'SELECT * FROM Patient where siteCode = :siteCode and serviceDiscontinued = 1')
  Future<List<Patient>> findDiscontinued(String siteCode);

  @Query('SELECT * FROM Patient WHERE id = :id')
  Future<Patient?> findById(int id);

  @Query('SELECT * FROM Patient WHERE uniqueId = :uniqueId')
  Future<Patient?> findByUniqueId(String uniqueId);

  @insert
  Future<int> insertRecord(Patient patient);

  @update
  Future<void> updateRecord(Patient patient);

  @Query("UPDATE Patient set synced = true WHERE id = :id")
  Future<void> updateSynced(int id);

  @Query('SELECT * FROM AssignedRegimen WHERE siteCode = :siteCode')
  Future<List<AssignedRegimen>> listAssignedRegimen(String siteCode);

  @Query(
      '''SELECT * FROM LastRefill WHERE siteCode = :siteCode AND dateNextRefill 
        BETWEEN :start AND :end''')
  Future<List<LastRefill>> listMissedRefill(
      String siteCode, DateTime start, DateTime end);

  @Query('''Update Patient set serviceDiscontinued = true, dateDiscontinued = 
      :dateDiscontinued, reasonDiscontinued = :reasonDiscontinued where id = :id''')
  Future<void> discontinueService(
      int id, DateTime dateDiscontinued, String reasonDiscontinued);

  @Query("delete from Patient")
  Future<void> deleteAll();
}
