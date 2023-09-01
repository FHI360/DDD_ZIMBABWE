import 'package:floor/floor.dart';
import 'package:impilo/flutter_flow/flutter_flow_util.dart';

@entity
class Refill {
  @PrimaryKey(autoGenerate: true)
  int? id;
  DateTime date;
  String regimen;
  String patientId;
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

  Map<String, dynamic> toJson() {
    return {
      'date': date.toIso8601String().substring(0, 10),
      'patient': {'id': patientId},
      'dateNextRefill': dateNextRefill.toIso8601String().substring(0, 10),
      'missedDoses': missedDoses,
      'adverseIssues': adverseIssues,
      'regimen': regimen,
      'quantityPrescribed': quantityPrescribed,
      'quantityDispensed': quantityDispensed,
      'organisation': {'id': FFAppState().code}
    };
  }
}
