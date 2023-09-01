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
  DateTime nextAppointmentDate;
  DateTime nextRefillDate;
  bool serviceDiscontinued;
  String reasonDiscontinued;
  DateTime dateDiscontinued;
  String uuid;
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
      this.nextAppointmentDate,
      this.nextRefillDate,
      this.serviceDiscontinued,
      this.reasonDiscontinued,
      this.dateDiscontinued,
      this.uuid,
      this.synced);

  factory Patient.fromJson(Map<String, dynamic> row) => Patient(
      null,
      row['givenName'],
      row['familyName'],
      row['hospitalNumber'],
      row['uniqueId'] ?? '',
      DateTime.parse(row['dateOfBirth']),
      row['sex'],
      row['phoneNumber'],
      row['assignedRegimen'],
      row['facility'],
      row['siteCode'],
      row['address'],
      DateTime.tryParse(row['lastClinicVisit'] ?? '') ?? DateTime(1900),
      DateTime.tryParse(row['lastRefillDate'] ?? '') ?? DateTime(1900),
      DateTime.tryParse(row['nextAppointmentDate'] ?? '') ?? DateTime(1900),
      DateTime.tryParse(row['nextRefillDate'] ?? '') ?? DateTime(1900),
      false,
      '',
      DateTime(1970),
      row['id'],
      true);

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
        'lastClinicVisit': lastClinicVisit.toIso8601String(),
        'lastRefillDate': lastRefillDate.toIso8601String(),
        'serviceDiscontinued': serviceDiscontinued,
        'reasonDiscontinued': reasonDiscontinued,
        'dateDiscontinued': dateDiscontinued.toIso8601String()
      };
}
