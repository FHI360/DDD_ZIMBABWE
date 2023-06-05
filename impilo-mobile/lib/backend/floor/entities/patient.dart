import 'package:floor/floor.dart';

@entity
class Patient {
  @PrimaryKey(autoGenerate: true)
  int? id;
  String givenName;
  String familyName;
  String hospitalNo;
  String uniqueId;
  DateTime dateOfBirth;
  String sex;
  String phoneNumber;
  String assignedRegimen;
  String facility;
  String siteCode;
  String address;
  DateTime lastClinicVisit;
  DateTime lastRefillDate;
  DateTime nextTptDate;
  DateTime nextAppointmentDate;
  DateTime nextViralLoadDate;
  DateTime nextCervicalCancerDate;
  DateTime nextVisitDate;
  bool serviceDiscontinued;
  String reasonDiscontinued;
  DateTime dateDiscontinued;
  bool synced;

  Patient(
      this.id,
      this.givenName,
      this.familyName,
      this.hospitalNo,
      this.uniqueId,
      this.dateOfBirth,
      this.sex,
      this.phoneNumber,
      this.assignedRegimen,
      this.facility,
      this.siteCode,
      this.address,
      this.lastClinicVisit,
      this.lastRefillDate,
      this.nextTptDate,
      this.nextAppointmentDate,
      this.nextCervicalCancerDate,
      this.nextViralLoadDate,
      this.nextVisitDate,
      this.serviceDiscontinued,
      this.reasonDiscontinued,
      this.dateDiscontinued,
      this.synced);

  factory Patient.fromJson(Map<String, dynamic> row) => Patient(
      row['id'],
      row['givenName'],
      row['familyName'],
      row['hospitalNo'],
      row['uniqueId'],
      row['dateOfBirth'],
      row['sex'],
      row['phoneNumber'],
      row['assignedRegimen'],
      row['facility'],
      row['siteCode'],
      row['address'],
      row['lastClinicVisit'],
      row['lastRefillDate'],
      row['nextTptDate'],
      row['nextAppointmentDate'],
      row['nextViralLoadDate'],
      row['nextCervicalCancerDate'],
      row['nextVisitDate'],
      row['serviceDiscontinued'],
      row['reasonDiscontinued'],
      row['dateDiscontinued'],
      row['synced']);

  Map<String, dynamic> toJson() => {
        'id': id,
        'givenName': givenName,
        'familyName': familyName,
        'hospitalNo': hospitalNo,
        'uniqueId': uniqueId,
        'dateOfBirth': dateOfBirth.toIso8601String(),
        'sex': sex,
        'phoneNumber': phoneNumber,
        'assignedRegimen': assignedRegimen,
        'facility': facility,
        'siteCode': siteCode,
        'address': address,
        'lastClinicVisit':
            lastClinicVisit != null ? lastClinicVisit.toIso8601String() : null,
        'lastRefillDate':
            lastRefillDate != null ? lastRefillDate.toIso8601String() : null,
        'nextTptDate':
            nextTptDate != null ? nextTptDate.toIso8601String() : null,
        'nextViralLoadDate': nextAppointmentDate != null
            ? nextAppointmentDate.toIso8601String()
            : null,
        'nextCervicalCancerDate': nextCervicalCancerDate != null
            ? nextCervicalCancerDate.toIso8601String()
            : null,
        'nextVisitDate':
            nextVisitDate != null ? nextVisitDate.toIso8601String() : null,
        'serviceDiscontinued': serviceDiscontinued,
        'reasonDiscontinued': reasonDiscontinued,
        'dateDiscontinued':
            dateDiscontinued != null ? dateDiscontinued.toIso8601String() : null
      };
/*@override
  List<Object?> get props => [uniqueId, hospitalNo];*/
}
