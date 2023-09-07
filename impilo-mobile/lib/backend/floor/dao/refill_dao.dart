import 'package:floor/floor.dart';
import 'package:impilo/backend/floor/entities/refill.dart';

import '../entities/devolve.dart';

@DatabaseView('''
WITH Estimated AS (
	SELECT * FROM (
		SELECT quantityDispensed, regimen, dateNextRefill, patientId, siteCode,
			ROW_NUMBER() OVER(PARTITION BY patientId ORDER BY dateNextRefill DESC) rn 
		FROM Refill JOIN Patient p ON patientId = p.uuid	
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
    p ON patientId = p.uuid ORDER BY givenName, familyName, sex    
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

@DatabaseView(
    '''SELECT SUM(quantityDispensed) AS quantity, regimen, barcode, siteCode FROM Refill
      JOIN patient p ON p.uuID = patientId GROUP BY regimen, barcode, siteCode''',
    viewName: 'BarcodeDispense')
class BarcodeDispense {
  final int quantity;
  final String siteCode;
  final String regimen;
  final String barcode;

  BarcodeDispense(this.quantity, this.siteCode, this.regimen, this.barcode);
}

@dao
abstract class RefillDao {
  @Query('SELECT * FROM Refill')
  Future<List<Refill>> findAll();

  @Query('SELECT * FROM Refill WHERE id = :id')
  Stream<Refill?> findById(int id);

  @Query('SELECT * FROM Refill where synced = false')
  Future<List<Refill>> findUnSynced();

  @Query('SELECT * FROM Refill WHERE patientId = :patientId')
  Future<List<Refill>> findByPatient(String patientId);

  @Query('SELECT * FROM Refill WHERE patientId = :patientId AND date = :date')
  Future<List<Refill>> findByPatientAndDate(int patientId, DateTime date);

  @insert
  Future<void> insertRecord(Refill refill);

  @update
  Future<int> updateRecord(Refill refill);

  @Query("DELETE FROM Refill WHERE date <= :date ")
  Future<void> deleteOlderThan(DateTime date);

  @Query('SELECT COUNT(*) > 0 FROM Refill WHERE synced = 0')
  Future<bool?> hasUnSynced();

  @Query("UPDATE Refill SET synced = 1")
  Future<void> updateAllSynced();

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

  @Query('''
    SELECT * FROM BarcodeDispense WHERE siteCode = :siteCode AND regimen = :regimen
    AND barcode = :barcode
  ''')
  Future<BarcodeDispense?> barcodeQuantity(
      String siteCode, String regimen, String barcode);
}


@dao
abstract class DevolveDao {
  @Query('SELECT * FROM Devolve WHERE synced = 0')
  Future<List<Devolve>> findUnSynced();

  @Query(
      'SELECT * FROM Devolve WHERE patientId = :patientId ORDER BY date DESC LIMIT 1')
  Future<Devolve?> findByPatient(String patientId);

  @insert
  Future<void> insertRecord(Devolve devolve);

  @Query("DELETE FROM Devolve")
  Future<void> deleteAll();

  @Query('SELECT COUNT(*) > 0 FROM Devolve WHERE synced= 0')
  Future<bool?> hasUnSynced();

  @Query("UPDATE Devolve SET synced = 1")
  Future<void> updateAllSynced();
}
