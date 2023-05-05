import 'package:equatable/equatable.dart';
import 'package:floor/floor.dart';

@entity
class Inventory {
  @PrimaryKey(autoGenerate: true)
  int? id;
  final String uniqueId;
  final String regimen;
  final int quantity;
  final String batchNo;
  final String barcode;
  final String siteCode;
  final DateTime expiryDate;

  Inventory(this.id, this.uniqueId, this.regimen, this.quantity, this.batchNo,
      this.barcode, this.siteCode, this.expiryDate);

  /*@override
  List<Object?> get props => [uniqueId];*/
}
