#!/bin/bash
#

export DISPLAY=:8.0

builddate=$( date +%Y%m%d )
buildtime=$( date +%H%M )

optLocal=/opt/local/eclipse
cd $optLocal

eclipseBuildId=I20091103-0941
basebuilder=$optLocal/I1103-0941/eclipse

pdeDir=$( find $basebuilder/ -name "org.eclipse.pde.build_*" | sort | head -1  )
buildfile=$pdeDir/scripts/build.xml
cpLaunch=$( find $basebuilder/ -name "org.eclipse.equinox.launcher_*.jar" \
  | sort | head -1 )
cpAndMain="$cpLaunch org.eclipse.equinox.launcher.Main"

relengDir=$optLocal/org.eclipse.ui.releng
javaHome=/opt/local/ibm-java2-i386-50


buildLabel=I${builddate}-${buildtime}
buildTopDir=${optLocal}/builds/${buildLabel}
buildDir=${buildTopDir}/${buildLabel}
mkdir -p $buildDir

$javaHome/bin/java  -cp $cpAndMain \
  -application org.eclipse.ant.core.antRunner \
  -noSplash \
  -buildfile $buildfile \
  -Dbuilder=$relengDir/builder \
  -Dbuilddate=${builddate} \
  -Dbuildtime=${buildtime} \
  -DeclipseBuildId=${eclipseBuildId} \
  -DbuildDir=${optLocal}/builds \
  -DlogExtension=.xml


sendComplete () {
mailx -s "[success] Build ${buildLabel}" \
  pwebster@ca.ibm.com <$buildDir/results/text/mail.txt
}

sendFailures () {
mailx -s "[fail] Build ${buildLabel}" \
  pwebster@ca.ibm.com <$buildDir/results/text/mail.txt
}

cat $buildDir/results/text/*.txt >$buildDir/results/text/mail.txt
bytes=$( cat $buildDir/results/text/mail.txt | wc -c )
if (( $bytes == 0 )); then
mv $buildDir/results/text/mail.txt $buildDir/results/text/pass.txt
else
mv $buildDir/results/text/mail.txt $buildDir/results/text/fail.txt
fi 

cp $optLocal/runBuild.log $buildDir
rm -rf ${buildTopDir}/plugins ${buildTopDir}/tests
cp -r $buildDir $HOME/builds

if [ ! -e $optLocal/done.txt ]; then
at now + 10 minutes <<EOF
cd $optLocal
cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/eclipse update \
  -d org.eclipse.ui.releng
cp org.eclipse.ui.releng/builder/runBuild.sh $optLocal
/bin/bash -l $optLocal/runBuild.sh >$optLocal/runBuild.log 2>&1
EOF
fi

