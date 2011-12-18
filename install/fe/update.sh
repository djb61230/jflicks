mkdir repository
cd repository
wget -r -nd -A jar -I /repository/ http://www.jflicks.org/repository/
cd ..
rsync -avz --existing repository/ bundle/
rm -rf repository
