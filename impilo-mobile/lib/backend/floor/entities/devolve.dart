import 'package:floor/floor.dart';

@entity
class Devolve {
  @PrimaryKey(autoGenerate: true)
  int? id;
  String? reasonDiscontinued;
  DateTime date;
  String outletCode;
  String patientId;
  bool? synced;

  Devolve(
      {this.id,
        this.reasonDiscontinued,
        required this.date,
        required this.outletCode,
        required this.patientId,
        required this.synced});

  factory Devolve.instance() {
    return Devolve(
        date: DateTime(1900),
        outletCode: '',
        patientId: '',
        synced: false);
  }

  Map<String, dynamic> toJson() {
    return {
      'date': date.toIso8601String(),
      'patient': {'id': patientId},
      'organisation': {'id': outletCode},
      'reasonDiscontinued': reasonDiscontinued
    };
  }
}