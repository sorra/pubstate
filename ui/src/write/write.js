import isEqual from 'lodash/isEqual'
import Common from '../common.js'


const EditorSelector = '#content-edit'
const AutosaveInterval = 3000

class Write {

  constructor(element, targetId, draftId) {
    this.$root = $(element)
    this.targetId = targetId
    this.draftId = draftId
    
    // treat unchanged data as initial draft
    this.savedDraft = this.getData(true)
    this.scheduleAutosave()

    this.$root.find('.btn-submit').tooltip({
      placement: 'top',
      trigger: 'manual'
    })

    this.$root.submit(ev => {
      this.onSubmit(ev)
    })

    this.editor = this.createEditor()

    this.setupFormatSelect()
  
  }

  onSubmit(ev) {
    ev.preventDefault()

    let data = this.getData()

    if (data.title.length == 0) {
      $('#title-edit').fadeOut().fadeIn()
      Common.popAlert('Title is empty, please input.')
      return
    }

    if (data.content.length == 0) {
      $('#editor-wrapper').fadeOut().fadeIn()
      Common.popAlert('Content is empty, please input.')
      return
    }

    let $form = this.$root
    let $submit = $form.find('.btn-submit')

    let url = $form.attr('action')
    data.draftId = this.draftId
    
    $.post(url, data)
      .done(resp => {
        window.location = resp
      })
      .fail(resp => {
        Common.popAlert('Publish failed because: ' + msg)
        $submit.prop('disabled', false)
      })

    $submit.prop('disabled', true)
  }

  createEditor() {
    let format = $('#format').val()
    if (format == 'MARKDOWN') {
      return this.createMarkdownEditor()
    } else if (format == 'HTML') {
      return this.createRichtextEditor()
    }
  }

  createMarkdownEditor() {
    return {
      simplemde: new SimpleMDE({
        element: $(EditorSelector)[0],
        autoDownloadFontAwesome: false,
        spellChecker: false
      }),
      getContent: function () {
        return this.simplemde.value()
      },
      remove: function() {
        this.simplemde.toTextArea()
      }
    }
  }

  createRichtextEditor() {
    tinymce.init({
      selector: EditorSelector,
      language: 'zh_CN',
      plugins: 'advlist autolink link image lists preview code codesample table fullscreen autoresize',
      menubar: 'view edit insert format table',
      toolbar: 'undo redo | formatselect | bold italic strikethrough | bullist numlist outdent indent | codesample | removeformat'
    })

    return {
      getContent: function () {
        return tinymce.activeEditor.getContent()
      },
      remove: function() {
        tinymce.remove(EditorSelector)
      }
    }
  }

  setupFormatSelect() {
    $('.format-select a').click((event) => {
      let $target = $(event.target)
      $target.closest('.format-select').find('.dropdown-toggle').text($target.text())
      $('#format').val($target.data('value'))
      $target.closest('.dropdown-menu').find('a').show()
      $target.hide()
      
      this.editor.remove()
      this.editor = this.createEditor()
    })
  }

  getData() {
    return {
      format: $('#format').val(),
      title: $('#title-edit').val(),
      content: this.editor == null ? $('#content-edit').val() : this.editor.getContent()
    }
  }

  scheduleAutosave() {
    setTimeout(() => {
      this.saveDraft()
    }, AutosaveInterval)
  }

  saveDraft() {
    let liveData = this.getData()
    if (isEqual(liveData, this.savedDraft)) {
      this.scheduleAutosave()
      return
    }

    let payload = {
      draftId: this.draftId,
      targetId: this.targetId,
      format: liveData.format,
      title: liveData.title,
      content: liveData.content,
      format: liveData.format
    }

    $.post("/drafts/save", payload)
      .done(respDraftId => {
        if (respDraftId) {
          this.draftId = respDraftId
          this.savedDraft = liveData
          Common.popAlert('Draft is autosaved', 'info', 1000)
        } else {
          console.info('Draft autosave is skipped by the server')
        }
      }).fail(resp => {
        Common.popAlert('Draft autosave failed: ' + Common.toErrorMsg(resp), 'error')
      }).always(() => {
        this.scheduleAutosave()
      })
  }
}

window.Write = Write
