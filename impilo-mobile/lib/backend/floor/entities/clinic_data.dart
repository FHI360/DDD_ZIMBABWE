import 'package:floor/floor.dart';
import 'package:impilo/app_state.dart';

@Entity(tableName: 'clinic')
class ClinicData {
  @PrimaryKey(autoGenerate: true)
  int? id;
  int? systolic;
  int? diastolic;
  double? weight;
  double? temperature;
  String patientId;
  final DateTime date;
  bool? coughing;
  bool? swelling;
  bool? sweating;
  bool? fever;
  bool? weightLoss;
  bool? tbReferred;
  final bool synced;

  ClinicData(
      this.id,
      this.systolic,
      this.diastolic,
      this.weight,
      this.temperature,
      this.patientId,
      this.date,
      this.coughing,
      this.swelling,
      this.sweating,
      this.fever,
      this.weightLoss,
      this.tbReferred,
      this.synced);

  Map<String, dynamic> toJson() => {
    'systolic': systolic,
    'diastolic': diastolic,
    'weight': weight,
    'temperature': temperature,
    'patient': {'id': patientId},
    'organisation': {'id': FFAppState().code},
    'date': date.toIso8601String().substring(0, 10),
    'coughing': coughing,
    'swelling': swelling,
    'sweating': sweating,
    'fever': fever,
    'weightLoss': weightLoss,
    'tbReferred': tbReferred
  };
}
