function toggleNav(){
  document.querySelector('.navbar').classList.toggle('open');
}

function stubSubmit(formEl, successMsg){
  formEl.addEventListener('submit', function(e){
    e.preventDefault();
    const msgBox = formEl.querySelector('.js-form-msg');
    if(msgBox){
      msgBox.style.display = 'block';
      msgBox.className = 'alert alert-info js-form-msg';
      msgBox.textContent = successMsg || 'Saved locally — backend servlet not connected yet.';
    } else {
      alert(successMsg || 'Backend servlet not connected yet.');
    }
  });
}

document.addEventListener('DOMContentLoaded', function(){
  const page = document.body.getAttribute('data-page');
  if(page){
    document.querySelectorAll('[data-nav]').forEach(function(a){
      if(a.getAttribute('data-nav') === page) a.classList.add('active');
    });
  }
});
