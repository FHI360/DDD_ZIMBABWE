import 'package:floor/floor.dart';
import 'package:impilo/backend/floor/entities/inventory.dart';

@DatabaseView('''select quantity, siteCode, regimen from Inventory''',
    viewName: 'InventoryQuantity')
class InventoryQuantity {
  final String siteCode;
  final String regimen;
  final int quantity;

  InventoryQuantity(this.siteCode, this.regimen, this.quantity);
}

@DatabaseView(
    '''SELECT COUNT(quantity) > 0 AS isAvailable, siteCode FROM Inventory 
    GROUP BY siteCode''',
    viewName: 'InventoryAvailability')
class InventoryAvailability {
  final bool isAvailable;
  final String siteCode;

  InventoryAvailability(this.isAvailable, this.siteCode);
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

  @Query('Update Inventory set quantity = :quantity WHERE id = :id')
  Future<void> updateQuantity(int id, int quantity);

  @Query('SELECT * FROM InventoryAvailability WHERE siteCode = :siteCode')
  Future<InventoryAvailability?> checkAvailability(String siteCode);

  @Query(
      '''SELECT * FROM Inventory WHERE siteCode = :siteCode and regimen = :regimen 
          and quantity > 0 order by expiryDate limit 1''')
  Future<Inventory?> getNonZeroInventory(String siteCode, String regimen);

  @Query(
      'select * from InventoryQuantity where siteCode = :siteCode and regimen = :regimen')
  Future<List<InventoryQuantity>> selectInventoryQuantity(
      String siteCode, String regimen);

  @insert
  Future<int> insertRecord(Inventory inventory);

  @update
  Future<void> updateRecord(Inventory inventory);

  @Query("delete from Inventory where id = :id")
  Future<void> deleteById(int id);
}
