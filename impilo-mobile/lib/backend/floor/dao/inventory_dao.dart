import 'package:floor/floor.dart';
import 'package:impilo/backend/floor/entities/inventory.dart';

@DatabaseView('''select balance, siteCode, regimen from Inventory''',
    viewName: 'InventoryQuantity')
class InventoryQuantity {
  final int balance;
  final String siteCode;
  final String regimen;

  InventoryQuantity(this.balance, this.siteCode, this.regimen);
}

@DatabaseView(
    '''SELECT COUNT(balance) > 0 AS isAvailable, siteCode FROM Inventory 
    GROUP BY siteCode''',
    viewName: 'InventoryAvailability')
class InventoryAvailability {
  final bool isAvailable;
  final String siteCode;

  InventoryAvailability(this.isAvailable, this.siteCode);
}

@DatabaseView(
    '''SELECT SUM(balance) AS quantity, regimen, barcode, siteCode FROM Inventory
      GROUP BY regimen, barcode, siteCode''',
    viewName: 'BarcodeQuantity')
class BarcodeQuantity {
  final int quantity;
  final String siteCode;
  final String regimen;
  final String barcode;

  BarcodeQuantity(this.quantity, this.siteCode, this.regimen, this.barcode);
}

@dao
abstract class InventoryDao {
  @Query(
      'SELECT * FROM Inventory where siteCode = :siteCode order by expiryDate')
  Future<List<Inventory>> findAll(String siteCode);

  @Query(
      'SELECT * FROM Inventory where siteCode = :siteCode and regimen = :regimen order by expiryDate')
  Future<List<Inventory>> findByRegimen(String siteCode, String regimen);

  @Query('SELECT * FROM Inventory WHERE id = :id')
  Stream<Inventory?> findById(int id);

  @Query('SELECT * FROM Inventory WHERE uniqueId = :uniqueId')
  Stream<Inventory?> findByUniqueId(String uniqueId);

  @Query(
      'SELECT * FROM Inventory WHERE uniqueId = :uniqueId and regimen = :regimen')
  Future<List<Inventory>> findByUniqueIdAndRegimen(
      String uniqueId, String regimen);

  @Query('Update Inventory set balance = :balance, synced = 0 WHERE id = :id')
  Future<void> updateBalance(int id, int balance);

  @Query('SELECT * FROM InventoryAvailability WHERE siteCode = :siteCode')
  Future<InventoryAvailability?> checkAvailability(String siteCode);

  @Query('''
    SELECT * FROM BarcodeQuantity WHERE siteCode = :siteCode AND regimen = :regimen
    AND barcode = :barcode
  ''')
  Future<BarcodeQuantity?> barcodeQuantity(String siteCode, String regimen, String barcode);

  @Query('''
  SELECT SUM(balance) FROM Inventory WHERE siteCode = :siteCode AND uniqueId = :uniqueId
  ''')
  Future<int?> issuedQuantity(String siteCode, String uniqueId);

  @Query(
      '''SELECT * FROM Inventory WHERE siteCode = :siteCode and regimen = :regimen 
          and balance > 0 order by expiryDate limit 1''')
  Future<Inventory?> getNonZeroInventory(String siteCode, String regimen);

  @Query(
      'select * from InventoryQuantity where siteCode = :siteCode and regimen = :regimen')
  Future<List<InventoryQuantity>> selectInventoryQuantity(
      String siteCode, String regimen);

  @Query('''
    SELECT COALESCE(SUM(balance), 0) FROM Inventory WHERE regimen = :regimen AND 
      siteCode = :siteCode
  ''')
  Future<int?> balanceForRegimen(String regimen, String siteCode);

  @insert
  Future<int> insertRecord(Inventory inventory);

  @update
  Future<void> updateRecord(Inventory inventory);

  @Query('UPDATE Inventory SET acknowledged = 1, synced = 0 WHERE reference = :reference')
  Future<void> acknowledge(String reference);

  @Query('SELECT COUNT(*) > 0 FROM Inventory WHERE synced = 0')
  Future<bool?> hasUnSynced();

  @Query('SELECT * FROM Inventory WHERE synced = 0')
  Future<List<Inventory>> findUnSynced();

  @Query("UPDATE Inventory SET synced = 1")
  Future<void> updateAllSynced();

  @Query("delete from Inventory")
  Future<void> deleteAll();
}
