## Database

### Notes:
- Each company will have two corresponding databases
- Have two solution to connect with these databases
    - ssh by terminal
    - mock it into product
- Some company will have both source for HKS (cơ sở) and HSI (phái sinh), each source will have a different set of DB
- Most of the information below will apply to HKS unless specify otherwise

### (Tip) Checking the name of available Oracle database on remote machine using ssh

For everyone want to discover what database is available on remote machine without knowing the exact name of the database, follow the below steps:

  - Ssh to remote machine using info in the SSH section
  - run the below command to list all the user on remote machine
  ``` bash
  cat /etc/passwd
  ``` 
  - You should see some kind of user with the name *oracle* (Our standard setup should have this user to manage oracle server)
  - Switch to this user (this user should not have any password associated as of the time of this writing)
  ``` bash
  sudo su - oracle
  ```
  - Using sqlplus to manage oracle db (This should not prompt any password)
  ``` bash
  sqlplus / as sysdba
  ```
  - Run oracle command to show db
  ``` sql
  show pdbs;
  ```

### (Tip) Quick way to generate hashed password from raw password
```bash
echo -n <raw-pasword> | openssl pkeyutl -encrypt -pubin -inkey <path-to-public-key (keystore file without ssl extension)> | base64 -w 0
```
As the hashed password is depended on which public key is used, unless you have a different public key which you need to regenerated yourself, you could use the below hashed password

### Mock DB of different company


#### GJS - DB2 (branch: release/gjs)

  - URL: 
    - _jdbc:db2://18.166.46.205:50000/hksfo_
    - _jdbc:db2://18.166.46.205:50000/hksbo_
  - Username/Password
    - _winvest/PQ22wor6_
  - Hash password
    - _1d75a0047b81a46a79e64450790f21a66cd000590d49848c68cdb5368891baa2e4e7bf08698a81cb2b2af424178dadc256b3488dbdd725fab74ffa9092e75a2bd53b1220436672b2a5acd8bf2c1277bc3a969f4a852c0f8ca58268320d1db271e58400451397044063e7f482540fa25a4bbb8e7d7ebd3fbc963d8eb39cd0bb93_

#### EIF - DB2 (branch: release/eif)

  - URL:
    - _jdbc:db2://94.190.216.70:33003/HKSFO_
    - _jdbc:db2://94.190.216.70:33003/HKSBO_
  - Username/Password:
    - _Winvest/PQ22wor6_
  - Hash password: 
    - _1d75a0047b81a46a79e64450790f21a66cd000590d49848c68cdb5368891baa2e4e7bf08698a81cb2b2af424178dadc256b3488dbdd725fab74ffa9092e75a2bd53b1220436672b2a5acd8bf2c1277bc3a969f4a852c0f8ca58268320d1db271e58400451397044063e7f482540fa25a4bbb8e7d7ebd3fbc963d8eb39cd0bb93_

#### FUBON - Oracle (branch: release/fubon)

  - URL:
    - _jdbc:oracle:thin@//94.190.216.70:33002/hksfo_
    - _jdbc:oracle:thin@//94.190.216.70:33002/hksbo_
  - Username/Password:
    - _Winvest/PQ22wor6_
  - Hash password:
    - _1d75a0047b81a46a79e64450790f21a66cd000590d49848c68cdb5368891baa2e4e7bf08698a81cb2b2af424178dadc256b3488dbdd725fab74ffa9092e75a2bd53b1220436672b2a5acd8bf2c1277bc3a969f4a852c0f8ca58268320d1db271e58400451397044063e7f482540fa25a4bbb8e7d7ebd3fbc963d8eb39cd0bb93_

#### CHIYU - Oracle (branch core: release/ncb + branch futurix: release/chiyu)

  - URL:
    - jdbc:oracle:thin@//94.190.216.70:33015/hksfo
    - jdbc:oracle:thin@//94.190.216.70:33015/hksbo
  - Username/Password:
    - _Winvest/PQ22wor6_
  - Hash password:
    - _1d75a0047b81a46a79e64450790f21a66cd000590d49848c68cdb5368891baa2e4e7bf08698a81cb2b2af424178dadc256b3488dbdd725fab74ffa9092e75a2bd53b1220436672b2a5acd8bf2c1277bc3a969f4a852c0f8ca58268320d1db271e58400451397044063e7f482540fa25a4bbb8e7d7ebd3fbc963d8eb39cd0bb93_

#### CSCI - DB2 (branch: release/csci)

  - URL:
    - _jdbc:db2://18.162.211.10:50000/CSCIFO_
    - _jdbc:db2://18.162.211.10:50000/CSCIFO_
  - Username/Password:
    - _Winvest/PQ22wor6_
  - Hash password:
    - _1d75a0047b81a46a79e64450790f21a66cd000590d49848c68cdb5368891baa2e4e7bf08698a81cb2b2af424178dadc256b3488dbdd725fab74ffa9092e75a2bd53b1220436672b2a5acd8bf2c1277bc3a969f4a852c0f8ca58268320d1db271e58400451397044063e7f482540fa25a4bbb8e7d7ebd3fbc963d8eb39cd0bb93_

#### TICKRS - Oracle (branch core: release/tickrs + branch futurix: release/tickrs)

  - URL:
    - HKS:
      - _jdbc:oracle:thin@//94.190.216.70:53072/fodb_
      - _jdbc:oracle:thin@//94.190.216.70:53072/bodb_
    - HSI:
      - _jdbc:oracle:thin@//94.190.216.70:53072/futfo_
      - _jdbc:oracle:thin@//94.190.216.70:53072/futbo_
  - Username/Password:
      - HKS: _winvest/PQ22wor6_
      - HSI: _winvest/PQ22wor6_
  - Hash password:
    - HKS: _1d75a0047b81a46a79e64450790f21a66cd000590d49848c68cdb5368891baa2e4e7bf08698a81cb2b2af424178dadc256b3488dbdd725fab74ffa9092e75a2bd53b1220436672b2a5acd8bf2c1277bc3a969f4a852c0f8ca58268320d1db271e58400451397044063e7f482540fa25a4bbb8e7d7ebd3fbc963d8eb39cd0bb93_
    - HSI: _1d75a0047b81a46a79e64450790f21a66cd000590d49848c68cdb5368891baa2e4e7bf08698a81cb2b2af424178dadc256b3488dbdd725fab74ffa9092e75a2bd53b1220436672b2a5acd8bf2c1277bc3a969f4a852c0f8ca58268320d1db271e58400451397044063e7f482540fa25a4bbb8e7d7ebd3fbc963d8eb39cd0bb93_

### SSH by terminal

#### AAIM

```
Hostname: POC004-AAIM
URL:  poc004-aaim.tx-tech.com
Ssh:  94.190.216.70:23004
User: wvadmin
Password: grreatP@ssw0rd4hk
```

#### Fubon

```
Hostname: POC002-Fubon
URL: poc002-fubon.tx-tech.com
Ssh: 94.190.216.70:23002
User: wvadmin
Password: grreatP@ssw0rd4hk
```

#### EIF

```
Hostname: POC003-EIF
URL: poc003-eif.tx-tech.com
Ssh 94.190.216.70:23003
User: wvadmin
Password: grreatP@ssw0rd4hk
```

#### BEA

```
Hostname: POC006-BEA
URL: poc006-bea.tx-tech.com
Ssh 94.190.216.70:23006
User: wvadmin
Password: P@ssw0rdt6adbin
```

#### TICKRS

```
Hostname: VM3072
URL: vm3072-tickrs.tx-tech.com
Ssh: 94.190.216.70:43072
User: wvadmin
Password: P@ssw0rdt6adbin
```
