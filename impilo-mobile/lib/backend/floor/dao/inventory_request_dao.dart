import 'package:floor/floor.dart';
import 'package:impilo/backend/floor/entities/inventory_request.dart';

@dao
abstract class InventoryRequestDao {
  @Query('SELECT * FROM InventoryRequest where siteCode = :siteCode order by date')
  Future<List<InventoryRequest>> findAll(String siteCode);

  @Query(
      'SELECT * FROM InventoryRequest where siteCode = :siteCode and regimen = :regimen order by date')
  Future<List<InventoryRequest>> findRegimen(String siteCode, String regimen);

  @Query('SELECT * FROM InventoryRequest WHERE id = :id')
  Stream<InventoryRequest?> findById(int id);

  @Query('SELECT * FROM InventoryRequest WHERE uniqueId = :uniqueId')
  Future<List<InventoryRequest>> findByUniqueId(String uniqueId);

  @Query('''
  Update InventoryRequest SET quantityFulfilled = quantityFulfilled  + :bottles 
    WHERE uniqueId = :uniqueId
  ''')
  Future<void> fulfilled(int bottles, String uniqueId);

  @insert
  Future<int> insertRecord(InventoryRequest inventory);

  @update
  Future<void> updateRecord(InventoryRequest inventory);

  @Query("delete from Inventory where id = :id")
  Future<void> deleteById(int id);

  @Query('SELECT COUNT(*) > 0 FROM InventoryRequest WHERE synced = 0')
  Future<bool?> hasUnSynced();

  @Query("UPDATE InventoryRequest SET synced = 1")
  Future<void> updateAllSynced();

  @Query('SELECT * FROM InventoryRequest where synced = 0')
  Future<List<InventoryRequest>> findUnSynced();

  @Query("delete from InventoryRequest")
  Future<void> deleteAll();
}
