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
  Stream<InventoryRequest?> findByUniqueId(String uniqueId);

  @Query('Update InventoryRequest set fulfilled = 1 WHERE uniqueId = :uniqueId')
  Future<void> fulfilled(String uniqueId);

  @Query('Update InventoryRequest set acknowledged = 1 WHERE uniqueId = :uniqueId')
  Future<void> acknowledged(String uniqueId);

  @insert
  Future<int> insertRecord(InventoryRequest inventory);

  @update
  Future<void> updateRecord(InventoryRequest inventory);

  @Query("delete from Inventory where id = :id")
  Future<void> deleteById(int id);
}
