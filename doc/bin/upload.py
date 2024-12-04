"""
    upload content to (create pages in) mediawiki
    Author: Sören Kemmann
"""

import requests
import json
import os
from pathlib import Path
import sys, getopt

def main(argv):
   inputfile = ''
   outputfile = ''
   actionUrl = ''
   baseDir = ''
   extension = ''
   lgname = ''
   lgpassword = ''
   category = ''
   pagename = ''
   try:
      opts, args = getopt.getopt(argv,"hvu:b:e:a:c:l:p:",["url=","basedir=","ext=","application=","category=", "login=","password="])
   except getopt.GetoptError:
      print ('upload.py -v -u <url> -b <basedir> -e <ext> -a <application> -c <category> -l <login> -p <password>')
      sys.exit(2)
   verbose = False
   for opt, arg in opts:
      if opt == '-h':
         print ('upload.py -v -u <url> -b <basedir> -e <ext> -a <application> -c <category> -l <login> -p <password>')
         sys.exit()
      elif opt in ("-v", "--verbose"):
         verbose  = True
      elif opt in ("-u", "--url"):
         actionUrl  = arg
      elif opt in ("-b", "--basedir"):
         baseDir  = arg
      elif opt in ("-e", "--ext"):
         extension  = arg
      elif opt in ("-a", "--application"):
         pagename  = arg
      elif opt in ("-c", "--category"):
         category  = arg
      elif opt in ("-l", "--login"):
         lgname = arg
      elif opt in ("-p", "--password"):
         lgpassword  = arg
   if verbose:
      print ("URL: ", actionUrl)
      print ("BaseDir: ", baseDir)
      print ("Extension: ", extension)
      print ("PageName: ", pagename)
      print ("Category: ", category)
      print ("Login: ", lgname)
      print ("Password: ", lgpassword)

   
   # actionUrl = "https://wiki.datapart-factoring.de/api.php"
   # baseDir = os.environ['WikiPagePath']
   # extension = os.environ['WikiPageExtension']
   # pagename = os.environ['ApplicationName']
   # lgname = "Dpadmin@gitlab"
   # lgpassword = "917560vj67gb164q069uecvus95unbtc"
   
   session = requests.Session()
   # Step 1: Retrieve a login token via action api
   actionParams = {
       "action": "query",
       "meta": "tokens",
       "type": "login",
       "format": "json"
   }
   if verbose:
       print ("Trying to retrieve Login Token ... ")
   response = session.get(url=actionUrl, params=actionParams)
   jsonData = response.json()
   LOGIN_TOKEN = jsonData["query"]["tokens"]["logintoken"]
   if verbose:
       print ("Login Token: ", LOGIN_TOKEN)
   # Step 2: Send a post request to login. Use of main account for login is not
   # supported. Obtain credentials via Special:BotPasswords
   # (https://www.mediawiki.org/wiki/Special:BotPasswords) for lgname & lgpassword
   loginParams = {
       "action": "login",
       "lgname": lgname,
       "lgpassword": lgpassword,
       "format": "json",
       "lgtoken": LOGIN_TOKEN
   }
   
   response = session.post(url=actionUrl, data=loginParams)
   jsonData = response.json()
   
   # Step 3: Obtain a CSRF token
   csrfParams = {
       "action": "query",
       "meta":"tokens",
       "format":"json"
   }
   
   response = session.get(url=actionUrl, params=csrfParams)
   jsonData = response.json()
   CSRF_TOKEN = jsonData["query"]["tokens"]["csrftoken"]
   if verbose:
       print ("CSRF Token: ", CSRF_TOKEN)
   
   # Step 4: POST request to edit a page
   
   
   directory = os.fsencode(baseDir)
   for root,d_names,f_names in os.walk(directory):
      for f in f_names:
          filename = os.fsdecode(f)
          if filename.endswith(extension): 
              titel = ''
              if category != '':
                  titel = category + ":"
              titel = titel + pagename
              if Path(filename).stem != "main":
                  titel = titel + "/" + os.fsdecode(os.path.relpath(os.path.join(root, os.fsencode(Path(filename).stem)), directory))
              if verbose: 
                 print (titel)
              with open(os.path.join(root, f), 'r') as contentfile:
                  pageText = contentfile.read()
              pageParams = {
                  "action": "edit",
                  "title": titel,
                  "token": CSRF_TOKEN,
                  "format": "json",
                  "text": pageText
              }
              response = session.post(url=actionUrl, data=pageParams)
              jsonData = response.json()
              if verbose:
                 print(jsonData)
              continue

if __name__ == "__main__":
   main(sys.argv[1:])




