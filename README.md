![Image of DIRBS Logo](https://avatars0.githubusercontent.com/u/42587891?s=100&v=4)

# DCP Android App
## License
Copyright (c) 2019 Qualcomm Technologies, Inc.

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted (subject to the limitations in the disclaimer below) provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Qualcomm Technologies, Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
* The origin of this software must not be misrepresented; you must not claim that you wrote the original software. If you use this software in a product, an acknowledgment is required by displaying the trademark/log as per the details provided here: https://www.qualcomm.com/documents/dirbs-logo-and-brand-guidelines
* Altered source versions must be plainly marked as such, and must not be misrepresented as being the original software.
* This notice may not be removed or altered from any source distribution.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## Features
- Enter or Scan IMEI
- Verify the integrity of TAC from GSMA database
- Report counterfiet devices
- View search history
- Send feedback to administrator
- Mutlilingual, currently supports English and Vietnamese

## System Requirements
### Software Requirements
Below mentioned are the software requirements.
- JDK 1.8 or more
- Android Studio 3.5
- Target Android SDK v29
- Minimum Android version 14
- Gradle 5.4.1
- Android Plugin version 3.5.0
### Hardware Requirements
Below are the hardware requirements to run DCP.
- At least 512 MB of RAM
- At least 1G of disk space
- Camera
- Android Device
- Active Internet Connection

## Configuration
- To change the logo of app, go to app/res/drawable folder and paste logo file but make sure the file name should be dcp_logo.png
- To change the colors of the app, go to app/res/values/colors.xml file and mention hex color code of your required color
- To change app icon, update all PNG files named “ic_launcher” in folders starting with “mipmap”
- To change DCP backend API base URL open root_directory/app/build.gradle and update value of BASE_URL in buildTypes

## Architecture
DCP Android application is developed using MVP (Model View Presenter) architecture.
### Dependencies
DCP app utilizes some open source libraries to meet its functional requirements. The libraries used and the purpose of their usage are
- AndroidX libraries for designing user interface
- Retrofit and OkHTTP3 for making network calls
- Barcode Scanner for scanning barcode of IMEI
- RxJava and RxAndroid for reactive programming
- ButterKnife for binding views and callbacks to fields and methods
- Espresso for unit testing
### File Structure
In DCP source code files are structured in a way that files related to a screen are placed in a folder having sub folders for model, view, presenter and network calls. There are some other folders for adapters, network related files and utility files.
