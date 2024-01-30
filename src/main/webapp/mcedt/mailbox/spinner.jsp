<%--

    Copyright (c) 2014-2015. KAI Innovations Inc. All Rights Reserved.
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
<style>
.spinner {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    z-index: 1234;
    display:none;
    background-color: rgba(255, 255, 255, 0.9);
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
    overflow: hidden;
    opacity: 1;
    transition: opacity 0.3s ease;
}

#img-spinner {
    width: 100px;
    height: 77px;
}

.screen {
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    height: 100%;
    width: 100%;
    margin: 0;
    padding: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 101;
    display: none;
    opacity: 1;
    transition: opacity 0.3s ease;
}

.active-spinner {
    display: block;
}
</style>

<script type="text/javascript">

// if locked is true: can't click away
// if locked is false: can click away from it
function ShowSpin(locked)
{
    let screen = document.getElementById("screen");
    let spinner = document.getElementById("spinner");
    
    screen.classList.add("active-spinner");
    spinner.classList.add("active-spinner");

    if (locked)
    {
        screen.removeEventListener("click", HideSpin);
    }
    else
    {
        screen.addEventListener("click", HideSpin);
    }
}

function HideSpin()
{
    let screen = document.getElementById("screen");
    let spinner = document.getElementById("spinner");
    
    screen.classList.remove("active-spinner");
    spinner.style.opacity = "0";
    
    setTimeout(function() {
        spinner.classList.remove("active-spinner");
        spinner.style.opacity = "1";
    }, 300);
}
</script>
<div id="screen" class="screen"></div>
<div id="spinner" class="spinner">
    <img id="img-spinner" src="<%=request.getContextPath() %>/images/spinner.jpg" alt="Loading"/>
</div>