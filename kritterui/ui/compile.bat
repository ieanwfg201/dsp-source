FOR /D C:\tools\play-2.2.3\repository\cache\com.kritter* %%X IN (com.kritter*) DO RD /S /Q "%%X" 
FOR /D %%X IN (C:\tools\play-2.2.3\repository\cache\com.kritter*) DO RD /S /Q "%%X"
play compile
