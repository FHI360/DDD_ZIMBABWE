import 'package:floor/floor.dart';

@entity
class Refill {
  @PrimaryKey(autoGenerate: true)
  int? id;
  DateTime date;
  String regimen;
  int patientId;
  int quantityPrescribed;
  int quantityDispensed;
  DateTime dateNextRefill;
  bool synced;
  bool? missedDoses;
  bool? adverseIssues;
  String? barcode;

  Refill(
      this.id,
      this.date,
      this.regimen,
      this.patientId,
      this.quantityPrescribed,
      this.quantityDispensed,
      this.dateNextRefill,
      this.missedDoses,
      this.adverseIssues,
      this.barcode,
      this.synced);

/*@override
  List<Object?> get props =>
      [date, patientId, quantityPrescribed, quantityDispensed];*/
}
