import 'package:equatable/equatable.dart';
import 'package:floor/floor.dart';

@entity
class InventoryRequest {
  @PrimaryKey(autoGenerate: true)
  int? id;
  final String uniqueId;
  final String regimen;
  final int quantity;
  final bool fulfilled;
  final bool acknowledged;
  final String siteCode;
  final DateTime date;
  final bool synced;

  InventoryRequest(this.id, this.uniqueId, this.regimen, this.quantity, this.fulfilled,
      this.acknowledged, this.siteCode, this.date, this.synced);

  Map<String, dynamic> toJson() {
    return {
      'date': date.toIso8601String(),
      'regimen': regimen,
      'quantity': quantity,
      'siteCode': siteCode,
      'uniqueId': uniqueId
    };
  }

  /*@override
  List<Object?> get props => [uniqueId];*/
}