import 'package:equatable/equatable.dart';
import 'package:floor/floor.dart';

@entity
class InventoryRequest {
  @PrimaryKey(autoGenerate: true)
  int? id;
  final String uniqueId;
  final String regimen;
  final int quantity;
  int quantityFulfilled = 0;
  final String siteCode;
  final DateTime date;
  final bool synced;

  InventoryRequest(this.id, this.uniqueId, this.regimen, this.quantity,
      this.siteCode, this.date, this.synced, this.quantityFulfilled);

  Map<String, dynamic> toJson() {
    return {
      'date': date.toIso8601String(),
      'regimen': regimen,
      'quantity': quantity,
      'siteCode': siteCode,
      'uniqueId': uniqueId
    };
  }
}