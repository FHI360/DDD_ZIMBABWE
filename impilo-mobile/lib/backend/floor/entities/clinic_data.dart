import 'package:floor/floor.dart';

@Entity(tableName: 'clinic')
class ClinicData {
  @PrimaryKey(autoGenerate: true)
  int? id;
  int? systolic;
  int? diastolic;
  double? weight;
  double? temperature;
  final int patientId;
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

/*@override
  List<Object?> get props => [patientId, date];*/
}
