<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>

<%
  if(session.getValue("user") == null)    response.sendRedirect("../logout.jsp");
  int oox=0, ooy=0;
  if(request.getParameter("oox")!=null) oox += Integer.parseInt(request.getParameter("oox"));
  if(request.getParameter("ooy")!=null) ooy += Integer.parseInt(request.getParameter("ooy"));
%>
<%@ page import="java.util.*, java.sql.*, oscar.*"
	errorPage="/errorpage.jsp"%>

<html>
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title>ANTENATAL RECORD</title>
<link rel="stylesheet" href="antenatalrecordprint.css">
<script language="JavaScript">
<!--		

var ox = <%=oox%>;
var oy = <%=ooy%>;
function ff(x,y,w,h,name) { //need escape to name for ' and "
  x = eval(ox+x);
  y = eval(oy+y);
  document.writeln('<div ID="bdiv1" STYLE="position:absolute; visibility:visible; z-index:2; left:'+x+'px; top:'+y+'px; width:'+w+'px; height:'+h+'px;"> ');
  document.writeln(name);
  document.writeln('</div>');
}

//-->
</SCRIPT>
</head>
<body onLoad="setfocus()" topmargin="0" leftmargin="1" rightmargin="1"
	bgcolor="navy">
<img src="../images/formar1_99_12.gif">

<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=50+oox%>px; top:<%=74+ooy%>px; width:400px; height:20px;">
<%=Misc.JSEscape(request.getParameter("xml_name"))%></div>

<script language="JavaScript">
ff(750,10,50,20,'<span class="title"><a href=# onClick="window.print()">Print</a></span>' );
ff(750,40,50,20,'<span class="title"><a href=# onClick="history.go(-1);return false;">Back</a></span>' );
ff(192,0,300,20,'<span class="title">Antenatal Record 1</span>' );
ff(8,68,100,20,'<span class="tdname">Name</span>' );
ff(8,90,100,20,'<span class="tdname">Address</span>' );
ff(5,114,200,20,'<span class="smalltdname">Date of birth (yyyy/mm/dd)</span>' );
ff(113,113,100,20,'<span class="tdname">Age</span>' );
ff(145,113,200,20,'<span class="tdname">Marital status</span>' );
ff(240,113,200,20,'<span class="tdname">Education level</span>' );
ff(165,123,50,20,'<span class="tdname">M</span>' );
ff(194,123,50,20,'<span class="tdname">CL</span>' );
ff(224,123,50,20,'<span class="tdname">S</span>' );

ff(50,98,500,20,"<%=Misc.JSEscape(request.getParameter("xml_address"))%>" );
ff(25,126,100,20,"<%=Misc.JSEscape(request.getParameter("xml_dob"))%>");
ff(120,126,50,20,"<%=Misc.JSEscape(request.getParameter("xml_age"))%>" );
ff(153,123,20,20,"<%=Misc.JSEscape(request.getParameter("xml_msm")==null?"":"X")%>" );
ff(181,123,20,20,"<%=Misc.JSEscape(request.getParameter("xml_mscl")==null?"":"X")%>" );
ff(212,123,20,20,"<%=Misc.JSEscape(request.getParameter("xml_mss")==null?"":"X")%>" );
ff(240,126,200,20,"<%=Misc.JSEscape(request.getParameter("xml_el"))%>" );

ff(8,137,200,20,'<span class="tdname">Occupation</span>' );
ff(113,137,200,20,'<span class="tdname">Language</span>' );
ff(208,137,200,20,'<span class="tdname">Home phone</span>' );
ff(305,137,200,20,'<span class="tdname">Work phone</span>' );
ff(400,137,200,20,'<span class="tdname">Name of partner</span>' );
ff(550,137,200,20,'<span class="tdname">Age</span>' );
ff(580,137,200,20,'<span class="tdname">Occupation</span>' );

ff(8,148,140,20,"<%=Misc.JSEscape(request.getParameter("xml_occp"))%>" );
ff(113,148,140,20,"<%=Misc.JSEscape(request.getParameter("xml_lang"))%>" );
ff(213,148,140,20,"<%=Misc.JSEscape(request.getParameter("xml_hp"))%>" );
ff(310,148,140,20,"<%=Misc.JSEscape(request.getParameter("xml_wp"))%>" );
ff(400,148,200,20,"<%=Misc.JSEscape(request.getParameter("xml_nop"))%>" );
ff(555,148,50,20,"<%=Misc.JSEscape(request.getParameter("xml_page"))%>" );
ff(580,148,140,20,"<%=Misc.JSEscape(request.getParameter("xml_poccp"))%>" );

ff(8,160,200,20,'<span class="tdname">Birth attendants</span>' );
ff(150,160,200,20,'<span class="tdname">Family physician</span>' );
ff(320,160,200,20,'<span class="tdname">Newborn care</span>' );
ff(481,160,200,20,'<span class="tdname">Ethnic background of mother/father</span>' );
ff(25,175,50,20,'<span class="tdname">OBS</span>' );
ff(66,175,50,20,'<span class="tdname">FP</span>' );
ff(100,175,100,20,'<span class="tdname">Midwife</span>' );
ff(337,175,50,20,'<span class="tdname">Ped.</span>' );
ff(378,175,50,20,'<span class="tdname">FP</span>' );
ff(415,175,100,20,'<span class="tdname">Midwife</span>' );

ff(13,175,20,20,"<%=Misc.JSEscape(request.getParameter("xml_baobs")==null?"":"X")%>" );
ff(55,175,20,20,"<%=Misc.JSEscape(request.getParameter("xml_bafp")==null?"":"X")%>" );
ff(90,175,20,20,"<%=Misc.JSEscape(request.getParameter("xml_bam")==null?"":"X")%>" );
ff(10,195,200,20,"<%=Misc.JSEscape(request.getParameter("xml_ba"))%>" );
ff(150,175,200,100,"<%=(Misc.JSEscape(request.getParameter("xml_fphy"))+"").replace('\r', ' ')%>" );
ff(324,175,20,20,"<%=Misc.JSEscape(request.getParameter("xml_ncp")==null?"":"X")%>" );
ff(365,175,20,20,"<%=Misc.JSEscape(request.getParameter("xml_ncfp")==null?"":"X")%>" );
ff(400,175,20,20,"<%=Misc.JSEscape(request.getParameter("xml_ncm")==null?"":"X")%>" );
ff(320,195,200,20,"<%=Misc.JSEscape(request.getParameter("xml_nc"))%>" );

ff(23,216,200,20,'<span class="tdname">VBAC</span>' );
ff(23,236,200,20,'<span class="tdname">Repeat CS</span>' );
ff(81,214,200,20,'<span class="tdname">Allergies (list)</span>' );
ff(392,214,200,20,'<span class="tdname">Medications (list)</span>' );

ff(12,216,20,20,"<%=Misc.JSEscape(request.getParameter("xml_vbac")==null?"":"X")%>" );
ff(12,236,20,20,"<%=Misc.JSEscape(request.getParameter("xml_rcs")==null?"":"X")%>" );

ff(6,275,200,20,'<span class="tdname">Menstrual history(LMP):</span>' );
ff(230,275,200,20,'<span class="tdname">Cycle</span>' );
ff(354,278,200,20,'<span class="tdname">Regular</span>' );
ff(420,278,200,20,'<span class="tdname">EDB</span>' );

ff(121,271,100,20,"<%=Misc.JSEscape(request.getParameter("xml_lmp"))%>" );
ff(270,273,100,20,"<%=Misc.JSEscape(request.getParameter("xml_c"))%>" );
ff(342,277,20,20,"<%=Misc.JSEscape(request.getParameter("xml_r")==null?"":"X")%>" );
ff(453,273,100,20,"<%=Misc.JSEscape(request.getParameter("xml_edb"))%>" );
ff(550,298,100,20,"<%=Misc.JSEscape(request.getParameter("xml_fedb"))%>" );

ff(6,292,200,20,'<span class="tdname">Contraception:</span>' );
ff(24,306,200,20,'<span class="tdname">IUD</span>' );
ff(65,306,200,20,'<span class="tdname">Hormonal(type)</span>' );
ff(257,306,200,20,'<span class="tdname">Other</span>' );
ff(390,303,200,20,'<span class="tdname">Last used</span>' );

ff(12,306,20,20,"<%=Misc.JSEscape(request.getParameter("xml_iud")==null?"":"X")%>" );
ff(53,306,20,20,"<%=Misc.JSEscape(request.getParameter("xml_ht")==null?"":"X")%>" );
ff(140,306,200,20,"<%=Misc.JSEscape(request.getParameter("xml_htt"))%>" );
ff(244,306,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oc")==null?"":"X")%>" );
ff(286,303,200,20,"<%=Misc.JSEscape(request.getParameter("xml_ot"))%>" );
ff(440,298,100,20,"<%=Misc.JSEscape(request.getParameter("xml_lu"))%>" );

ff(6,334,200,20,'<span class="tdname">Gravida</span>' );
ff(66,334,200,20,'<span class="tdname">Term</span>' );
ff(93,334,200,20,'<span class="tdname">Prem</span>' );
ff(121,334,200,20,'<span class="tdname">No. of pregnancy loss(es)</span>' );
ff(141,347,200,20,'<span class="tdname">Ectopic</span>' );
ff(253,347,200,20,'<span class="tdname">Termination</span>' );
ff(377,347,200,20,'<span class="tdname">Spontaneous</span>' );
ff(513,347,200,20,'<span class="tdname">Stillborn</span>' );
ff(605,334,100,20,'<span class="tdname">Living</span>' );
ff(638,334,200,20,'<span class="tdname">Multipregnancy</span>' );
ff(639,349,50,20,'<span class="tdname">No.</span>' );

ff(12,348,100,20,"<%=Misc.JSEscape(request.getParameter("xml_gra"))%>" );
ff(70,348,100,20,"<%=Misc.JSEscape(request.getParameter("xml_term"))%>" );
ff(93,348,100,20,"<%=Misc.JSEscape(request.getParameter("xml_prem"))%>" );
ff(131,347,20,20,"<%=Misc.JSEscape(request.getParameter("xml_ecc")==null?"":"X")%>" );
ff(189,344,100,20,"<%=Misc.JSEscape(request.getParameter("xml_ect"))%>" );
ff(244,347,20,20,"<%=Misc.JSEscape(request.getParameter("xml_tec")==null?"":"X")%>" );
ff(312,344,100,20,"<%=Misc.JSEscape(request.getParameter("xml_tet"))%>" );
ff(367,347,20,20,"<%=Misc.JSEscape(request.getParameter("xml_spc")==null?"":"X")%>" );
ff(442,344,100,20,"<%=Misc.JSEscape(request.getParameter("xml_spt"))%>" );
ff(504,347,20,20,"<%=Misc.JSEscape(request.getParameter("xml_stc")==null?"":"X")%>" );
ff(555,344,100,20,"<%=Misc.JSEscape(request.getParameter("xml_stt"))%>" );
ff(605,348,100,20,"<%=Misc.JSEscape(request.getParameter("xml_liv"))%>" );
ff(653,350,200,20,"<%=Misc.JSEscape(request.getParameter("xml_mul"))%>" );

ff(12,395,50,20,'<span class="tdname">No.</span>' );
ff(38,395,50,20,'<span class="tdname">Year</span>' );
ff(73,388,50,20,'<span class="tdname">Sex</span>' );
ff(73,400,50,20,'<span class="tdname">M/F</span>' );
ff(98,388,100,20,'<span class="tdname">Gest. age</span>' );
ff(105,400,100,20,'<span class="tdname">(weeks)</span>' );
ff(158,388,100,20,'<span class="tdname">Birth</span>' );
ff(156,400,100,20,'<span class="tdname">weight</span>' );
ff(205,388,100,20,'<span class="tdname">Length</span>' );
ff(200,400,100,20,'<span class="tdname">of labour</span>' );
ff(273,388,100,20,'<span class="tdname">Place</span>' );
ff(270,400,100,20,'<span class="tdname">of birth</span>' );
ff(330,388,100,20,'<span class="tdname">Type of birth</span>' );
ff(332,400,150,20,'<span class="smalltdname">SVB CS Ass\'d</span>' );
ff(455,395,250,20,'<span class="tdname">Comments regarding pregnancy and birth</span>' );

ff(12,421,20,20,"1" );
ff(36,421,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1ye"))%>" );
ff(78,421,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1se"))%>" );
ff(100,421,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1ge"))%>" );
ff(148,421,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1bi"))%>" );
ff(200,421,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1le"))%>" );
ff(245,421,200,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1pl"))%>" );
ff(338,421,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1sv")==null?"":"X")%>" );
ff(353,421,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1cs")==null?"":"X")%>" );
ff(369,421,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1as")==null?"":"X")%>" );
//ff(392,421,340,20,"<%=Misc.JSEscape(request.getParameter("xml_oh1co"))%>" );

ff(12,439,20,20,"2" );
ff(36,439,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2ye"))%>" );
ff(78,439,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2se"))%>" );
ff(100,439,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2ge"))%>" );
ff(148,439,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2bi"))%>" );
ff(200,439,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2le"))%>" );
ff(245,439,200,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2pl"))%>" );
ff(338,439,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2sv")==null?"":"X")%>" );
ff(353,439,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2cs")==null?"":"X")%>" );
ff(369,439,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2as")==null?"":"X")%>" );
//ff(392,439,340,20,"<%=Misc.JSEscape(request.getParameter("xml_oh2co"))%>" );

ff(12,457,20,20,"3" );
ff(36,457,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3ye"))%>" );
ff(78,457,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3se"))%>" );
ff(100,457,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3ge"))%>" );
ff(148,457,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3bi"))%>" );
ff(200,457,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3le"))%>" );
ff(245,457,200,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3pl"))%>" );
ff(338,457,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3sv")==null?"":"X")%>" );
ff(353,457,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3cs")==null?"":"X")%>" );
ff(369,457,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3as")==null?"":"X")%>" );
//ff(392,457,340,20,"<%=Misc.JSEscape(request.getParameter("xml_oh3co"))%>" );

ff(12,476,20,20,"4" );
ff(36,476,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4ye"))%>" );
ff(78,476,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4se"))%>" );
ff(100,476,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4ge"))%>" );
ff(148,476,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4bi"))%>" );
ff(200,476,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4le"))%>" );
ff(245,476,200,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4pl"))%>" );
ff(338,476,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4sv")==null?"":"X")%>" );
ff(353,476,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4cs")==null?"":"X")%>" );
ff(369,476,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4as")==null?"":"X")%>" );
//ff(392,476,340,20,"<%=Misc.JSEscape(request.getParameter("xml_oh4co"))%>" );

ff(12,494,20,20,"5" );
ff(36,494,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5ye"))%>" );
ff(78,494,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5se"))%>" );
ff(100,494,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5ge"))%>" );
ff(148,494,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5bi"))%>" );
ff(200,494,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5le"))%>" );
ff(245,494,200,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5pl"))%>" );
ff(338,494,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5sv")==null?"":"X")%>" );
ff(353,494,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5cs")==null?"":"X")%>" );
ff(369,494,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5as")==null?"":"X")%>" );
//ff(392,494,340,20,"<%=Misc.JSEscape(request.getParameter("xml_oh5co"))%>" );

ff(12,512,20,20,"6" );
ff(36,512,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6ye"))%>" );
ff(78,512,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6se"))%>" );
ff(100,512,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6ge"))%>" );
ff(148,512,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6bi"))%>" );
ff(200,512,100,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6le"))%>" );
ff(245,512,200,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6pl"))%>" );
ff(338,512,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6sv")==null?"":"X")%>" );
ff(353,512,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6cs")==null?"":"X")%>" );
ff(369,512,20,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6as")==null?"":"X")%>" );
//ff(392,512,340,20,"<%=Misc.JSEscape(request.getParameter("xml_oh6co"))%>" );

ff(16,549,200,20,'<span class="tdname">Current Pregnancy</span>' );
ff(145,549,200,20,'<span class="tdname">Medical</span>' );
ff(225,549,200,20,'<span class="tdname">Yes</span>' );
ff(245,549,200,20,'<span class="tdname">No</span>' );
ff(295,549,200,20,'<span class="tdname">Genetic/Family</span>' );
ff(400,549,200,20,'<span class="tdname">Yes</span>' );
ff(420,549,200,20,'<span class="tdname">No</span>' );
ff(450,549,200,20,'<span class="tdname">Infection Discussion Topics</span>' );
ff(595,549,200,20,'<span class="tdname"><b>Physical examination</b></span>' );

ff(10,561,200,20,'<span class="smalltdname"><i>(check if positive)</i></span>' );
ff(7,571,200,20,'<span class="smalltdname">1.</span>' );
ff(25,571,200,20,'<span class="smalltdname">Bleeding</span>' );
ff(7,583,200,20,'<span class="smalltdname">2.</span>' );
ff(25,583,200,20,'<span class="smalltdname">Vomiting</span>' );
ff(7,595,200,20,'<span class="smalltdname">3.</span>' );
ff(25,595,200,20,'<span class="smalltdname">Smoking</span>' );
ff(25,604,200,20,'<span class="smalltdname">cig/day</span>' );
ff(7,619,200,20,'<span class="smalltdname">4.</span>' );
ff(25,619,200,20,'<span class="smalltdname">Drugs</span>' );
ff(7,632,200,20,'<span class="smalltdname">5.</span>' );
ff(25,632,200,20,'<span class="smalltdname">Alcohol</span>' );
ff(25,643,200,20,'<span class="smalltdname">drinks/day</span>' );
ff(7,655,200,20,'<span class="smalltdname">6.</span>' );
ff(25,655,200,20,'<span class="smalltdname">Infertility</span>' );
ff(7,667,200,20,'<span class="smalltdname">7.</span>' );
ff(25,667,200,20,'<span class="smalltdname">Radiation</span>' );
ff(7,679,200,20,'<span class="smalltdname">8.</span>' );
ff(25,679,200,20,'<span class="smalltdname">Occup./Env.</span>' );
ff(25,689,200,20,'<span class="smalltdname">hazards</span>' );

ff(102,572,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp1b")==null?"":"X")%>" );
ff(102,584,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp2v")==null?"":"X")%>" );
ff(66,605,100,20,"<%=Misc.JSEscape(request.getParameter("xml_cp3c"))%>" );
ff(102,608,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp3s")==null?"":"X")%>" );
ff(102,620,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp4d")==null?"":"X")%>" );
ff(69,641,100,20,"<%=Misc.JSEscape(request.getParameter("xml_cp5d"))%>" );
ff(102,644,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp5a")==null?"":"X")%>" );
ff(102,656,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp6i")==null?"":"X")%>" );
ff(102,668,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp7r")==null?"":"X")%>" );
ff(102,680,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cp8o")==null?"":"X")%>" );

ff(10,704,200,20,'<span class="smalltdname"><b>Nutrition Assessment</b></span>' );
ff(10,714,200,20,'<span class="smalltdname"><i>(check if positive)</i></span>' );
ff(8,726,200,20,'<span class="smalltdname">Folic acid/vitamins</span>' );
ff(8,738,200,20,'<span class="smalltdname">Milk products</span>' );
ff(8,750,200,20,'<span class="smalltdname">Diet</span>' );
ff(27,759,200,20,'<span class="smalltdname">Balanced</span>' );
ff(27,770,200,20,'<span class="smalltdname">Restricted</span>' );
ff(8,782,200,20,'<span class="smalltdname">Dietitian referral</span>' );

ff(102,727,20,20,"<%=Misc.JSEscape(request.getParameter("xml_nafa")==null?"":"X")%>" );
ff(102,739,20,20,"<%=Misc.JSEscape(request.getParameter("xml_namp")==null?"":"X")%>" );
ff(102,758,20,20,"<%=Misc.JSEscape(request.getParameter("xml_nadb")==null?"":"X")%>" );
ff(102,771,20,20,"<%=Misc.JSEscape(request.getParameter("xml_nadr")==null?"":"X")%>" );
ff(102,783,20,20,"<%=Misc.JSEscape(request.getParameter("xml_nadref")==null?"":"X")%>" );

ff(122,572,200,20,'<span class="smalltdname">9.</span>' );
ff(137,572,200,20,'<span class="smalltdname">Hypertension</span>' );
ff(122,584,200,20,'<span class="smalltdname">10.</span>' );
ff(137,584,200,20,'<span class="smalltdname">Endocrine/Diabetes</span>' );
ff(122,596,200,20,'<span class="smalltdname">11.</span>' );
ff(137,596,200,20,'<span class="smalltdname">Heart</span>' );
ff(122,608,200,20,'<span class="smalltdname">12.</span>' );
ff(137,608,200,20,'<span class="smalltdname">Renal/urinary tract</span>' );
ff(122,620,200,20,'<span class="smalltdname">13.</span>' );
ff(137,620,200,20,'<span class="smalltdname">Respiratory</span>' );
ff(122,632,200,20,'<span class="smalltdname">14.</span>' );
ff(137,632,200,20,'<span class="smalltdname">Liver/Hepatitis/GI</span>' );
ff(122,644,200,20,'<span class="smalltdname">15.</span>' );
ff(137,644,200,20,'<span class="smalltdname">Neurological</span>' );
ff(122,656,200,20,'<span class="smalltdname">16.</span>' );
ff(137,656,200,20,'<span class="smalltdname">Autoimmune</span>' );
ff(122,668,200,20,'<span class="smalltdname">17.</span>' );
ff(137,668,200,20,'<span class="smalltdname">Breast</span>' );
ff(122,680,200,20,'<span class="smalltdname">18.</span>' );
ff(137,680,200,20,'<span class="smalltdname">Gyn/PAP</span>' );
ff(122,692,200,20,'<span class="smalltdname">19.</span>' );
ff(137,692,200,20,'<span class="smalltdname">Hospitalizations</span>' );
ff(122,704,200,20,'<span class="smalltdname">20.</span>' );
ff(137,704,200,20,'<span class="smalltdname">Surgeries</span>' );
ff(122,716,200,20,'<span class="smalltdname">21.</span>' );
ff(137,716,200,20,'<span class="smalltdname">Anesthetics</span>' );
ff(122,728,200,20,'<span class="smalltdname">22.</span>' );
ff(137,728,200,20,'<span class="smalltdname">Hem./Transfusions</span>' );
ff(122,740,200,20,'<span class="smalltdname">23.</span>' );
ff(137,740,200,20,'<span class="smalltdname">Varicosities/Phlebitis</span>' );
ff(122,752,200,20,'<span class="smalltdname">24.</span>' );
ff(137,752,200,20,'<span class="smalltdname">Psychiatric illness</span>' );
ff(122,764,200,20,'<span class="smalltdname">25.</span>' );
ff(137,764,200,20,'<span class="smalltdname">Other</span>' );

ff(228,573,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m9hy")==null?"":"X")%>" );
ff(246,573,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m9hn")==null?"":"X")%>" );
ff(228,585,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m10ey")==null?"":"X")%>" );
ff(246,585,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m10en")==null?"":"X")%>" );
ff(228,597,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m11hy")==null?"":"X")%>" );
ff(246,597,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m11hn")==null?"":"X")%>" );
ff(228,609,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m12ry")==null?"":"X")%>" );
ff(246,609,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m12rn")==null?"":"X")%>" );
ff(228,621,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m13ry")==null?"":"X")%>" );
ff(246,621,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m13rn")==null?"":"X")%>" );
ff(228,633,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m14ly")==null?"":"X")%>" );
ff(246,633,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m14ln")==null?"":"X")%>" );
ff(228,645,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m15ny")==null?"":"X")%>" );
ff(246,645,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m15nn")==null?"":"X")%>" );
ff(228,657,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m16ay")==null?"":"X")%>" );
ff(246,657,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m16an")==null?"":"X")%>" );
ff(228,669,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m17by")==null?"":"X")%>" );
ff(246,669,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m17bn")==null?"":"X")%>" );
ff(228,681,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m18gy")==null?"":"X")%>" );
ff(246,681,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m18gn")==null?"":"X")%>" );
ff(228,693,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m19hy")==null?"":"X")%>" );
ff(246,693,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m19hn")==null?"":"X")%>" );
ff(228,705,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m20sy")==null?"":"X")%>" );
ff(246,705,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m20sn")==null?"":"X")%>" );
ff(228,717,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m21ay")==null?"":"X")%>" );
ff(246,717,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m21an")==null?"":"X")%>" );
ff(228,729,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m22hy")==null?"":"X")%>" );
ff(246,729,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m22hn")==null?"":"X")%>" );
ff(228,741,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m23vy")==null?"":"X")%>" );
ff(246,741,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m23vn")==null?"":"X")%>" );
ff(228,753,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m24py")==null?"":"X")%>" );
ff(246,753,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m24pn")==null?"":"X")%>" );
ff(228,765,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m25oy")==null?"":"X")%>" );
ff(246,765,20,20,"<%=Misc.JSEscape(request.getParameter("xml_m25on")==null?"":"X")%>" );
ff(136,780,200,20,"<%=Misc.JSEscape(request.getParameter("xml_m25"))%>" );

ff(267,571,200,20,'<span class="smalltdname">26.</span>' );
ff(283,571,200,20,'<span class="smalltdname">Age&gt;=35 at EDB</span>' );
ff(267,583,200,20,'<span class="smalltdname">27.</span>' );
ff(283,583,200,20,'<span class="smalltdname">"At risk" population</span>' );
ff(283,591,200,20,'<span class="minitdname">(Tay-Sach\'s, sicke cell,</span>' );
ff(283,600,200,20,'<span class="minitdname">thalassemia, etc.)</span>' );

ff(267,611,200,20,'<span class="smalltdname">28.</span>' );
ff(283,611,200,20,'<span class="smalltdname">Known teratogen exposure</span>' );
ff(283,619,200,20,'<span class="minitdname">(includes maternal diabetes)</span>' );
ff(267,630,200,20,'<span class="smalltdname">29.</span>' );
ff(283,630,200,20,'<span class="smalltdname">Previous birth defect</span>' );

ff(402,573,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g26ay")==null?"":"X")%>" );
ff(420,573,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g26an")==null?"":"X")%>" );
ff(402,585,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g27ay")==null?"":"X")%>" );
ff(420,585,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g27an")==null?"":"X")%>" );
ff(402,613,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g28ky")==null?"":"X")%>" );
ff(420,613,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g28kn")==null?"":"X")%>" );
ff(402,632,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g29py")==null?"":"X")%>" );
ff(420,632,20,20,"<%=Misc.JSEscape(request.getParameter("xml_g29pn")==null?"":"X")%>" );

ff(267,640,200,20,'<span class="smalltdname"><b>Family history of:</b></span>' );
ff(267,651,200,20,'<span class="smalltdname">30.</span>' );
ff(283,651,200,20,'<span class="smalltdname">Neural tube defects</span>' );
ff(267,662,200,20,'<span class="smalltdname">31.</span>' );
ff(283,662,200,20,'<span class="smalltdname">Development delay</span>' );
ff(267,673,200,20,'<span class="smalltdname">32.</span>' );
ff(283,673,200,20,'<span class="smalltdname">Congenital physical</span>' );
ff(283,681,200,20,'<span class="minitdname">anomalies (includes</span>' );
ff(283,689,200,20,'<span class="minitdname">congenital heart disease)</span>' );
ff(267,698,200,20,'<span class="smalltdname">33.</span>' );
ff(283,698,200,20,'<span class="smalltdname">Congenital hypotonias</span>' );
ff(267,710,200,20,'<span class="smalltdname">34.</span>' );
ff(283,710,200,20,'<span class="smalltdname">Chromosomal disease</span>' );
ff(283,718,200,20,'<span class="minitdname">(Down\'s, Turner\'s, etc.)</span>' );
ff(267,729,200,20,'<span class="smalltdname">35.</span>' );
ff(283,729,200,20,'<span class="smalltdname">Genetic disease</span>' );
ff(283,737,200,20,'<span class="minitdname">(cystic fibrosis, muscular</span>' );
ff(283,746,200,20,'<span class="minitdname">dystrophy, etc.)</span>' );
ff(267,757,200,20,'<span class="smalltdname">36.</span>' );
ff(283,757,200,20,'<span class="smalltdname">Further investigations</span>' );
ff(267,768,200,20,'<span class="smalltdname">37.</span>' );
ff(283,768,200,20,'<span class="smalltdname">MSS</span>' );
ff(283,777,200,20,'<span class="smalltdname">Offered</span>' );
ff(283,786,200,20,'<span class="smalltdname">Accepted</span>' );

ff(402,652,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh30ny")==null?"":"X")%>" );
ff(420,652,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh30nn")==null?"":"X")%>" );
ff(402,664,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh31dy")==null?"":"X")%>" );
ff(420,664,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh31dn")==null?"":"X")%>" );
ff(402,676,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh32cy")==null?"":"X")%>" );
ff(420,676,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh32cn")==null?"":"X")%>" );
ff(402,699,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh33cy")==null?"":"X")%>" );
ff(420,699,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh33cn")==null?"":"X")%>" );
ff(402,712,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh34cy")==null?"":"X")%>" );
ff(420,712,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh34cn")==null?"":"X")%>" );
ff(402,731,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh35gy")==null?"":"X")%>" );
ff(420,731,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh35gn")==null?"":"X")%>" );
ff(402,759,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh36fy")==null?"":"X")%>" );
ff(420,759,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh36fn")==null?"":"X")%>" );
ff(402,776,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh37oy")==null?"":"X")%>" );
ff(420,776,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh37on")==null?"":"X")%>" );
ff(402,788,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh37ay")==null?"":"X")%>" );
ff(420,788,20,20,"<%=Misc.JSEscape(request.getParameter("xml_fh37an")==null?"":"X")%>" );

ff(442,571,200,20,'<span class="smalltdname">38.</span>' );
ff(461,571,200,20,'<span class="smalltdname">STDs/Herpes</span>' );
ff(442,583,200,20,'<span class="smalltdname">39.</span>' );
ff(461,583,200,20,'<span class="smalltdname">HIV</span>' );
ff(442,595,200,20,'<span class="smalltdname">40.</span>' );
ff(461,595,200,20,'<span class="smalltdname">Varicella</span>' );
ff(442,607,200,20,'<span class="smalltdname">41.</span>' );
ff(461,607,200,20,'<span class="smalltdname">Toxo/CMV/Parvo</span>' );
ff(442,619,200,20,'<span class="smalltdname">42.</span>' );
ff(461,619,200,20,'<span class="smalltdname">TB/Other</span>' );

ff(572,573,20,20,"<%=Misc.JSEscape(request.getParameter("xml_idt38s")==null?"":"X")%>" );
ff(572,585,20,20,"<%=Misc.JSEscape(request.getParameter("xml_idt39h")==null?"":"X")%>" );
ff(572,597,20,20,"<%=Misc.JSEscape(request.getParameter("xml_idt40v")==null?"":"X")%>" );
ff(572,609,20,20,"<%=Misc.JSEscape(request.getParameter("xml_idt41t")==null?"":"X")%>" );
ff(500,617,200,20,'<span class="smalltdname"><%=Misc.JSEscape(request.getParameter("xml_idt42o"))%></span>' );
ff(572,621,20,20,"<%=Misc.JSEscape(request.getParameter("xml_idt42t")==null?"":"X")%>" );

ff(442,640,200,20,'<span class="smalltdname"><b>Psychosocial discussion topics</b></span>' );
ff(442,654,200,20,'<span class="smalltdname">43.</span>' );
ff(461,654,200,20,'<span class="smalltdname">Social support</span>' );
ff(442,666,200,20,'<span class="smalltdname">44.</span>' );
ff(461,666,200,20,'<span class="smalltdname">Couple\'s relationship</span>' );
ff(442,678,200,20,'<span class="smalltdname">45.</span>' );
ff(461,678,200,20,'<span class="smalltdname">Emotional/Depression</span>' );
ff(442,690,200,20,'<span class="smalltdname">46.</span>' );
ff(461,690,200,20,'<span class="smalltdname">Substance abuse</span>' );
ff(442,702,200,20,'<span class="smalltdname">47.</span>' );
ff(461,702,200,20,'<span class="smalltdname">Family violence</span>' );
ff(442,714,200,20,'<span class="smalltdname">48.</span>' );
ff(461,714,200,20,'<span class="smalltdname">Parenting concerns</span>' );

ff(572,656,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pdt43s")==null?"":"X")%>" );
ff(572,668,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pdt44c")==null?"":"X")%>" );
ff(572,680,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pdt45e")==null?"":"X")%>" );
ff(572,692,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pdt46s")==null?"":"X")%>" );
ff(572,704,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pdt47f")==null?"":"X")%>" );
ff(572,716,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pdt48p")==null?"":"X")%>" );

ff(442,734,200,20,'<span class="smalltdname"><b>Risk factors identified</b></span>' );

ff(590,565,100,20,'<span class="smalltdname">Ht.</span>' );
ff(652,565,100,20,'<span class="smalltdname">Wt.</span>' );
ff(590,587,200,20,'<span class="smalltdname">Pre-preg. wt.</span>' );
ff(590,608,100,20,'<span class="smalltdname">BP</span>' );

ff(602,565,20,20,"<%=Misc.JSEscape(request.getParameter("xml_peh"))%>" );
ff(672,565,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pew"))%>" );
ff(642,585,20,20,"<%=Misc.JSEscape(request.getParameter("xml_ppw"))%>" );
ff(610,608,20,20,"<%=Misc.JSEscape(request.getParameter("xml_pebp"))%>" );

ff(591,630,200,20,'<span class="smalltdname"><b>Checkmark if normal:</b></span>' );
ff(591,643,200,20,'<span class="smalltdname">Head, teeth, ENT</span>' );
ff(591,655,200,20,'<span class="smalltdname">Thyroid</span>' );
ff(591,667,200,20,'<span class="smalltdname">Chest</span>' );
ff(591,679,200,20,'<span class="smalltdname">Breasts</span>' );
ff(591,691,200,20,'<span class="smalltdname">Cardiovascular</span>' );
ff(591,703,200,20,'<span class="smalltdname">Abdomen</span>' );
ff(591,715,200,20,'<span class="smalltdname">Varicosities, extremities</span>' );
ff(591,727,200,20,'<span class="smalltdname">Neurological</span>' );
ff(591,739,200,20,'<span class="smalltdname">Pelvic architecture</span>' );
ff(591,750,200,20,'<span class="smalltdname">Ext. genitalia</span>' );
ff(591,761,200,20,'<span class="smalltdname">Cervix, vagina</span>' );
ff(591,771,200,20,'<span class="smalltdname">Uterus</span>' );
ff(640,771,200,20,'<span class="smalltdname">(no. of wks.)</span>' );
ff(591,785,200,20,'<span class="smalltdname">Adnexa</span>' );

ff(693,643,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinhe")==null?"":"X")%>" );
ff(693,655,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinth")==null?"":"X")%>" );
ff(693,667,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinch")==null?"":"X")%>" );
ff(693,679,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinbr")==null?"":"X")%>" );
ff(693,691,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinca")==null?"":"X")%>" );
ff(693,703,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinab")==null?"":"X")%>" );
ff(693,715,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinva")==null?"":"X")%>" );
ff(693,727,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinne")==null?"":"X")%>" );
ff(693,739,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinpe")==null?"":"X")%>" );
ff(693,751,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinex")==null?"":"X")%>" );
ff(693,763,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cince")==null?"":"X")%>" );
ff(622,771,20,20,'<span class="smalltdname"><%=Misc.JSEscape(request.getParameter("xml_cinun"))%></span>' );
ff(693,775,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinut")==null?"":"X")%>" );
ff(693,787,20,20,"<%=Misc.JSEscape(request.getParameter("xml_cinad")==null?"":"X")%>" );

ff(20,935,200,20,"<%=Misc.JSEscape(request.getParameter("xml_soa"))%>" );
ff(500,935,100,20,"<%=Misc.JSEscape(request.getParameter("xml_date"))%>" );

ff(6,919,300,20,'<span class="tdname">Signature of attendant</span>' );
ff(430,919,300,20,'<span class="tdname">Date (yyyy/mm/dd)</span>' );

ff(15,958,200,20,'<span class="smalltdname">0374-64 (99/12)</span>' );
ff(180,958,350,20,"<span class=\"smalltdname\">Canary - Mother's chart - forward to hospital  Pink - Attendant's copy  White - Infant's chart</span>" );
ff(665,958,200,20,'<span class="smalltdname">7530-4654</span>' );

</script>
<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=480+oox%>px; top:<%=ooy+175%>px; width:230px; height:30px;">
<pre><%=request.getParameter("xml_ebmf")%></pre></div>

<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=80+oox%>px; top:<%=ooy+228%>px; width:280px; height:50px;">
<span class="smalltdname"><%=request.getParameter("xml_Alert_demographicaccessory")%></span>
</div>
<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=395+oox%>px; top:<%=ooy+228%>px; width:280px; height:50px;">
<span class="smalltdname"><%=request.getParameter("xml_Medication_demographicaccessory")%></span>
</div>

<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=440+oox%>px; top:<%=ooy+748%>px; width:150px; height:60px;">
<span class="smalltdname"><%=request.getParameter("xml_rfi")%></span></div>

<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=12+oox%>px; top:<%=ooy+830%>px; width:300px; height:60px;">
<pre><%=request.getParameter("xml_comments")%></pre></div>

<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=392+oox%>px; top:<%=ooy+421%>px; width:340px; height:20px;">
<%=request.getParameter("xml_oh1co")%></div>
<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=392+oox%>px; top:<%=ooy+439%>px; width:340px; height:20px;">
<%=request.getParameter("xml_oh2co")%></div>
<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=392+oox%>px; top:<%=ooy+457%>px; width:340px; height:20px;">
<%=request.getParameter("xml_oh3co")%></div>
<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=392+oox%>px; top:<%=ooy+476%>px; width:340px; height:20px;">
<%=request.getParameter("xml_oh4co")%></div>
<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=392+oox%>px; top:<%=ooy+494%>px; width:340px; height:20px;">
<%=request.getParameter("xml_oh5co")%></div>
<div ID="bdiv1"
	STYLE="position:absolute; visibility:visible; z-index:2; left:<%=392+oox%>px; top:<%=ooy+512%>px; width:340px; height:20px;">
<%=request.getParameter("xml_oh6co")%></div>

</body>
</html>
