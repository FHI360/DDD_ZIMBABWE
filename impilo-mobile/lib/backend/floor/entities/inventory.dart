import 'package:floor/floor.dart';

@entity
class Inventory {
  @PrimaryKey(autoGenerate: true)
  int? id;
  final String uniqueId;
  final String reference;
  final String regimen;
  final int quantity;
  final int balance;
  final String batchNo;
  final String barcode;
  final String siteCode;
  final DateTime expiryDate;
  final String batchIssuanceId;
  bool acknowledged = false;
  bool synced = false;

  Inventory(
      this.id,
      this.uniqueId,
      this.reference,
      this.regimen,
      this.quantity,
      this.balance,
      this.acknowledged,
      this.batchNo,
      this.barcode,
      this.siteCode,
      this.expiryDate,
      this.batchIssuanceId);

  Map<String, dynamic> toJson() {
    return {
      'reference': reference,
      'balance': balance
    };
  }
}
