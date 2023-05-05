import 'package:floor/floor.dart';
import 'package:impilo/backend/floor/entities/refill.dart';

@DatabaseView('''
WITH Estimated AS (
	SELECT * FROM (
		SELECT quantityDispensed, regimen, dateNextRefill, patientId, siteCode,
			ROW_NUMBER() OVER(PARTITION BY patientId ORDER BY dateNextRefill DESC) rn 
		FROM Refill JOIN Patient p ON patientId = p.id	
	) e WHERE rn = 1
)
SELECT regimen, siteCode, SUM(quantityDispensed) qty, dateNextRefill 
  FROM Estimated GROUP BY regimen, siteCode, dateNextRefill
''', viewName: 'EstimatedRefill')
class EstimatedRefill {
  final String siteCode;
  final String regimen;
  final int qty;
  final DateTime dateNextRefill;

  EstimatedRefill(this.siteCode, this.regimen, this.qty, this.dateNextRefill);
}

@DatabaseView('''
  SELECT givenName, familyName, sex, dateOfBirth, quantityDispensed quantity, 
    hospitalNo, regimen, siteCode, dateNextRefill, date FROM Refill JOIN Patient 
    p ON patientId = p.id ORDER BY givenName, familyName, sex    
''', viewName: 'RefillInfo')
class RefillInfo {
  final String siteCode;
  final String familyName;
  final String givenName;
  final int quantity;
  final DateTime date;
  final DateTime dateNextRefill;
  final DateTime dateOfBirth;
  final String regimen;
  final String sex;
  final String hospitalNo;

  RefillInfo(
      this.siteCode,
      this.familyName,
      this.givenName,
      this.quantity,
      this.date,
      this.dateNextRefill,
      this.dateOfBirth,
      this.regimen,
      this.sex,
      this.hospitalNo);
}

@dao
abstract class RefillDao {
  @Query('SELECT * FROM Refill')
  Future<List<Refill>> findAll();

  @Query('SELECT * FROM Refill WHERE id = :id')
  Stream<Refill?> findById(int id);

  @Query('SELECT * FROM Refill where synced = false')
  Future<List<Refill>> findUnSynced();

  @Query('SELECT * FROM Refill WHERE patientId = :patientId ORDER BY date DESC')
  Future<List<Refill>> findByPatient(int patientId);

  @Query('SELECT * FROM Refill WHERE patientId = :patientId AND date = :date')
  Future<List<Refill>> findByPatientAndDate(int patientId, DateTime date);

  @insert
  Future<void> insertRecord(Refill refill);

  @update
  Future<int> updateRecord(Refill refill);

  @Query("delete from Refill where id = :id")
  Future<void> deleteById(int id);

  @Query('''SELECT regimen, siteCode, SUM(qty) qty, 1 AS dateNextRefill FROM 
          EstimatedRefill WHERE siteCode = :siteCode AND dateNextRefill BETWEEN 
          :start and :end GROUP BY regimen ORDER BY regimen, siteCode''')
  Future<List<EstimatedRefill>> estimatedRefill(
      String siteCode, DateTime start, DateTime end);

  @Query(
      '''SELECT * FROM RefillInfo WHERE siteCode = :siteCode AND date BETWEEN 
        :start and :end ORDER BY givenName, familyName''')
  Future<List<RefillInfo>> listRefillInfo(
      String siteCode, DateTime start, DateTime end);
}
