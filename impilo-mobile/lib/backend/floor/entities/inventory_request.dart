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

  InventoryRequest(this.id, this.uniqueId, this.regimen, this.quantity, this.fulfilled,
      this.acknowledged, this.siteCode, this.date);

  /*@override
  List<Object?> get props => [uniqueId];*/
}